package it.repix.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.flurry.android.FlurryAgent;

import net.simonvt.menudrawer.MenuDrawer;

// Referenced classes of package it.repix.android:
//            ExifHelper, DownloaderDialog, GL2JNIView, RightSideListView, 
//            SideListView, RepixWebView, PurchaseManager, GL2JNILib, 
//            Platform, WhatsNewDlg

public class RepixActivity extends Activity
{

    public static final boolean DEBUG = false;
    static final String DEFAULT_IMAGES[] = {
        "/assets/default/helsinki.jpg", "/assets/default/000001806840.jpg", "/assets/default/137461279.jpg"
    };
    public static final int RC_CAMERA = 40002;
    public static final int RC_GALLERY = 40001;
    public static final int RESTORE_DELAY = 500;
    public static final int RESTORE_INTERVAL = 0x493e0;
    public static final String TAG = "repix";
    public static final String URL_REPIX_FEED = "http://repix.it/android/feed";
    public static final String URL_REPIX_HELP = "http://repix.it/android150/help";
    static GL2JNIView glView;
    static RepixActivity instance;
    static Bitmap pendingBitmap = null;
    String currentToolbar;
    ExifHelper exif;
    boolean glViewHack;
    MenuDrawer menuDrawer;
    MenuItem menuRedo;
    MenuItem menuReset;
    MenuItem menuSave;
    MenuItem menuShareMore;
    MenuItem menuStoreClose;
    MenuItem menuUndo;
    boolean redoState;
    Handler restoreTimer;
    Runnable restoreTimerRunnable;
    ShareActionProvider shareActionProvider;
    MenuDrawer shareDrawer;
    HashMap shareInfo;
    RightSideListView sharelistview;
    SideListView sidelistview;
    RepixWebView webview;
    ViewGroup webviewlayout;

    public RepixActivity()
    {
        exif = new ExifHelper();
        restoreTimer = new Handler();
        glViewHack = false;
        restoreTimerRunnable = new Runnable() {
            public void run()
            {
                PurchaseManager.getInstance().restorePurchases(true);
                restoreTimer.postDelayed(this, 0x493e0L);
            }
        };
        redoState = true;
    }

    private void cancelAutoRestore()
    {
        restoreTimer.removeCallbacks(restoreTimerRunnable);
    }

    private void cleanupMemory()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                if(webview != null)
                {
                    webview.clearCache(false);
                }
            }
        });
        queueEvent(new Runnable() {
            public void run()
            {
                GL2JNILib.didReceiveMemoryWarning();
            }
        });
    }

    private void disableMenuKey()
    {
        ViewConfiguration viewconfiguration;
        Field field;
        try
        {
            viewconfiguration = ViewConfiguration.get(this);
            field = android.view.ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(field == null)
            {
                //break MISSING_BLOCK_LABEL_28;
            	return;
            }
            field.setAccessible(true);
            field.setBoolean(viewconfiguration, false);
        }
        catch(Exception exception)
        {
            return;
        }
    }

    public static void downloadCamu(Activity activity)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=com.sumoing.camu"));
        activity.startActivity(intent);
    }

    private File getCameraFile()
    {
        return new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Repix"), "camera.jpg");
    }

    static RepixActivity getInstance()
    {
        return instance;
    }

    private int getStartCount()
    {
        return getPreferences().getInt("count", 0);
    }

    private boolean handleSendImageIntent(Intent intent)
    {
        String s;
        String s1;
        try{
	        s = intent.getAction();
	        s1 = intent.getType();
	        if(!"android.intent.action.SEND".equals(s) || s1 == null || !s1.startsWith("image/"))
	        	return false;
	        Uri uri = (Uri)intent.getParcelableExtra("android.intent.extra.STREAM");
	        if(uri == null){
	        	alert("No image available", null);
	        	return false;
	        }
	        download(uri);
	        return true;
        }catch(IOException ioexception){
        	alert(ioexception.getMessage(), null);
            return false;
        }
    }

    private void incrementStartCount()
    {
        SharedPreferences sharedpreferences = getPreferences();
        android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
        int i = sharedpreferences.getInt("count", -1);
        if(i < 0)
        {
            i = 0;
        }
        editor.putInt("count", i + 1);
        editor.remove("android.test.purchased");
        editor.commit();
    }

    private void onRequestGalleryResult(int i, Intent intent)
    {
        Uri uri;
        if(i != -1)
            return;
        try{
	        uri = null;
	        if(intent != null)
	        {
	            uri = intent.getData();
	            if(uri == null && intent.getExtras() != null)
	            {
	                uri = (Uri)intent.getParcelableExtra("android.intent.extra.STREAM");
	            }
	        }
	        if(uri == null)
	        {
	            uri = Uri.fromFile(getCameraFile());
	        }
	        download(uri);
	        openEditor();
        }catch(IOException ioexception){}
    }

    private InputStream openInputStream(Uri uri)
        throws FileNotFoundException
    {
        if("file".equals(uri.getScheme()))
        {
            return new FileInputStream(uri.getPath());
        } else
        {
            return getContentResolver().openInputStream(uri);
        }
    }

    private void postAutoRestore()
    {
        restoreTimer.postDelayed(restoreTimerRunnable, 500L);
    }

    private String savePhoto(Bitmap bitmap)
        throws IOException
    {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String s = (new StringBuilder()).append(simpledateformat.format(new Date())).append(".jpg").toString();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Repix");
        file.mkdirs();
        File file1 = new File(file, s);
        FileOutputStream fileoutputstream = new FileOutputStream(file1);
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, fileoutputstream);
        fileoutputstream.close();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("_data", file1.getAbsolutePath());
        contentvalues.put("title", "Repix");
        contentvalues.put("mime_type", "image/jpeg");
        Uri uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentvalues);
        String as[];
        try
        {
            saveExif(uri);
        }
        catch(IOException ioexception) { }
        as = new String[1];
        as[0] = file1.toString();
        MediaScannerConnection.scanFile(this, as, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String s1, Uri uri1)
            {
            }
        });
        return uri.toString();
    }

    private void showWhatsNewIfVersionChanged()
    {
        cancelAutoRestore();
        postAutoRestore();
        try
        {
            String s = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            SharedPreferences sharedpreferences = getPreferences();
            if(!s.equals(sharedpreferences.getString("version", "")))
            {
                android.content.SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("version", s);
                editor.commit();
                showWhatsNew();
            }
            return;
        }
        catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            return;
        }
    }

    public void alert(final String message, final android.content.DialogInterface.OnClickListener positiveListener)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                (new android.app.AlertDialog.Builder(RepixActivity.this)).setMessage(message).setPositiveButton("OK", positiveListener).show();
            }
        });
    }

    public void closeStore()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                setHeadingTitle(R.string.app_name);
            }
        });
        queueEvent(new Runnable() {
            public void run()
            {
                GL2JNILib.closeStore();
                RepixActivity.glView.requestRender();
            }
        });
    }

    public void confirm(final String message, final android.content.DialogInterface.OnClickListener positiveListener)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                (new android.app.AlertDialog.Builder(RepixActivity.this)).setMessage(message).setPositiveButton("OK", positiveListener).setNegativeButton("Cancel", null).show();
            }
        });
    }

    String currentTimeAsExif()
    {
        return (new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")).format(new Date());
    }

    void download(Uri uri)
        throws IOException
    {
        int i1;
        Matrix matrix;
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        Bitmap bitmap;
        int i;
        int j;
        float f1;
        int k;
        int l;
        Bitmap bitmap1;
        String s;
        Bitmap bitmap2;
        String s1;
        int j1;
        try
        {
            InputStream inputstream = openInputStream(uri);
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputstream, null, options);
            inputstream.close();
            InputStream inputstream1 = openInputStream(uri);
            //options.outWidth;
            //options.outHeight;
            float f = Math.max((float)options.outWidth / (float)2048, (float)options.outHeight / (float)2048);
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int)Math.floor(f);
            options.inPurgeable = false;
            bitmap = BitmapFactory.decodeStream(inputstream1, null, options);
            inputstream1.close();
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            downloadHttp(uri);
            return;
        }
        catch(RuntimeException runtimeexception)
        {
            alert((new StringBuilder()).append("Failed to open image\n").append(runtimeexception.toString()).toString(), null);
            return;
        }
        if(bitmap == null);
        i = bitmap.getWidth();
        j = bitmap.getHeight();
        f1 = Math.max((float)i / (float)2048, (float)j / (float)2048);
        if(f1 >= 0.7F && f1 <= 1.0F)
        {
            //break MISSING_BLOCK_LABEL_205;
        	return;
        }
        k = (int)((float)i / f1);
        l = (int)((float)j / f1);
        bitmap1 = bitmap;
        bitmap = Bitmap.createScaledBitmap(bitmap, k, l, true);
        bitmap1.recycle();
        if(bitmap == null){
        	alert("Failed to open image", null);
            return;
        }
        s = getContentFilename(uri);
        if(s == null)
        {
            //break MISSING_BLOCK_LABEL_231;
        	return;
        }
        exif.readExif(s);
        s1 = exif.getAttribute("Orientation");
        i1 = 0;
        if(s1 == null)
        {
            //break MISSING_BLOCK_LABEL_262;
        	return;
        }
        try{
	        j1 = Integer.parseInt(s1);
	        i1 = j1;
        }catch(NumberFormatException numberformatexception){
        	i1 = 0;
        }
        if(!(i1 == 0 || i1 == 1)){
	        matrix = new Matrix();
	        switch(i1){
	        default:case 4:case 5:case 7:
	        	break;
	        case 3:
	        	matrix.postRotate(180F);
	        	break;
	        case 6:
	        	matrix.postRotate(90F);
	        	break;
	        case 8:
	        	matrix.postRotate(270F);
	        	break;
	        }
	        bitmap2 = bitmap;
	        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	        bitmap2.recycle();
        }
        setCurrentPhoto(bitmap);
        
    }

    public void downloadHttp(Uri uri)
        throws IOException
    {
        (new DownloaderDialog(uri)).show();
    }

    String getContentFilename(Uri uri)
        throws IOException
    {
        if(uri != null)
        {
            if("file".equals(uri.getScheme()))
            {
                return uri.getPath();
            }
            String as[] = {
                "_data"
            };
            Cursor cursor = getContentResolver().query(uri, as, null, null, null);
            if(cursor != null)
            {
                int i = cursor.getColumnIndexOrThrow("_data");
                cursor.moveToFirst();
                return cursor.getString(i);
            }
        }
        return null;
    }

    public SharedPreferences getPreferences()
    {
        return getPreferences(0);
    }

    void hideSplash()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                findViewById(R.id.splash).setVisibility(8);
                showWhatsNewIfVersionChanged();
            }
        });
    }

    public void hideToolbarButtons()
    {
        menuReset.setVisible(false);
        menuUndo.setVisible(false);
        menuRedo.setVisible(false);
        menuStoreClose.setVisible(false);
        menuSave.setVisible(false);
    }

    void initGL()
    {
    }

    void initUI()
    {
        glView = (GL2JNIView)findViewById(R.id.repix_gl);
        menuDrawer = (MenuDrawer)findViewById(R.id.drawer);
        menuDrawer.setTouchMode(0);
        shareDrawer = (MenuDrawer)findViewById(R.id.sharedrawer);
        shareDrawer.setTouchMode(0);
        menuDrawer.setDropShadowColor(0x80000000);
        shareDrawer.setDropShadowColor(0x80000000);
        sharelistview = (RightSideListView)findViewById(R.id.sharelistview);
        sidelistview = (SideListView)findViewById(R.id.sidelistview);
        webview = (RepixWebView)findViewById(R.id.webview);
        webviewlayout = (ViewGroup)findViewById(R.id.webviewlayout);
        webviewlayout.setVisibility(8);
        sidelistview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterview, View view, int i, long l)
            {
                view.performHapticFeedback(1);
                LeftSideListView.Options options = LeftSideListView.Options.values()[i];
                            
                switch(options)
                {
                default:
                    return;
                case CAMU: // '\001'
                    FlurryAgent.logEvent("menu:camu");
                    RepixActivity.downloadCamu(RepixActivity.this);
                    return;
                case EDITOR: // '\002'
                    FlurryAgent.logEvent("menu:editor");
                    closeStore();
                    closeStore();
                    openEditor();
                    return;
                case PHOTOS: // '\003'
                    FlurryAgent.logEvent("menu:gallery");
                    openGallery();
                    return;
                case CAMERA: // '\004'
                    FlurryAgent.logEvent("menu:camera");
                    openCamera();
                    return;
                case FEED: // '\005'
                    FlurryAgent.logEvent("menu:starters");
                    openFeed();
                    return;
                case STORE: // '\006'
                    FlurryAgent.logEvent("menu:store");
                    openStore();
                    return;
                case HELP: // '\007'
                    FlurryAgent.logEvent("menu:help");
                    break;
                }
                openHelp();
            }
        });
        setStatus(false);
    }

    public void log(String s)
    {
    }

    protected void onActivityResult(int i, int j, Intent intent)
    {
        super.onActivityResult(i, j, intent);
        switch(i)
        {
        default:
            return;

        case 40001: 
        case 40002: 
            onRequestGalleryResult(j, intent);
            return;

        case 1001: 
            PurchaseManager.getInstance().onRequestBuyResult(j, intent);
            return;
        }
    }

    public void onBackPressed()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                setHeadingTitle(R.string.app_name);
            }
        });
        if(menuDrawer.isMenuVisible())
        {
            menuDrawer.closeMenu();
            return;
        }
        if(webviewlayout.getVisibility() == 0)
        {
            openEditor();
            return;
        } else
        {
            queueEvent(new Runnable() {
                public void run()
                {
                    if(!GL2JNILib.closeStore())
                    {
                        GL2JNILib.didReceiveMemoryWarning();
                        moveTaskToBack(true);
                        return;
                    } else
                    {
                        RepixActivity.glView.requestRender();
                        openEditor();
                        return;
                    }
                }
            });
            return;
        }
    }

    protected void onCreate(Bundle bundle)
    {
        Log.d("repix", (new StringBuilder()).append("BOARD ").append(Build.BOARD).toString());
        Log.d("repix", (new StringBuilder()).append("FINGERPRINT ").append(Build.FINGERPRINT).toString());
        Log.d("repix", (new StringBuilder()).append("MANUFACTURER ").append(Build.MANUFACTURER).toString());
        Log.d("repix", (new StringBuilder()).append("PRODUCT ").append(Build.PRODUCT).toString());
        Log.d("repix", (new StringBuilder()).append("DEVICE ").append(Build.DEVICE).toString());
        super.onCreate(bundle);
        setContentView(R.layout.main);
        ActionBar actionbar = getActionBar();
        actionbar.setTitle("");
        actionbar.show();
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        boolean flag;
        try
        {
            DownloaderDialog.install(getCacheDir());
        }
        catch(IOException ioexception) { }
        instance = this;
        initUI();
        if(!handleSendImageIntent(getIntent()))
        {
            incrementStartCount();
            String s = DEFAULT_IMAGES[getStartCount() % DEFAULT_IMAGES.length];
            setCurrentPhoto(BitmapFactory.decodeStream(getClass().getResourceAsStream(s)));
        }
        getWindow().setSoftInputMode(3);
        getResources().getConfiguration();
        if(GL2JNILib.getDeviceType() == 0 || GL2JNILib.getDeviceType() == 3)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if(flag)
        {
            setRequestedOrientation(1);
        }
        PurchaseManager.getInstance().bind();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        for(int i = 0; i < menu.size(); i++)
        {
            MenuItem menuitem1 = menu.getItem(i);
            if(menuitem1.getItemId() == 0)
            {
                menuitem1.setVisible(false);
            }
        }

        menuSave = menu.findItem(R.id.editor_save);
        menuReset = menu.findItem(R.id.editor_reset);
        menuUndo = menu.findItem(R.id.editor_undo);
        menuRedo = menu.findItem(R.id.editor_redo);
        menuStoreClose = menu.findItem(R.id.editor_store_close);
        menuShareMore = menu.findItem(R.id.share_more);
        SubMenu submenu = menuSave.getSubMenu();
        SubMenu submenu1 = menuShareMore.getSubMenu();
        shareInfo = new HashMap();
        if(submenu != null)
        {
            PackageManager packagemanager = getPackageManager();
            int j = 1;
            while(j < sharelistview.getCount()) 
            {
                ResolveInfo resolveinfo = sharelistview.getResolveInfoAt(j);
                String s = resolveinfo.loadLabel(packagemanager).toString();
                MenuItem menuitem;
                if(sharelistview.isPreferred(resolveinfo.activityInfo.packageName))
                {
                    menuitem = submenu.add(s);
                } else
                {
                    menuitem = submenu1.add(s);
                }
                menuitem.setIcon(resolveinfo.loadIcon(packagemanager));
                shareInfo.put(menuitem, resolveinfo);
                j++;
            }
        }
        ImageButton imagebutton = new ImageButton(this);
        imagebutton.setImageDrawable(menuUndo.getIcon());
        imagebutton.setBackgroundResource(0);
        menuUndo.setActionView(imagebutton);
        menuUndo.getActionView().setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View view)
            {
                shareDrawer.performHapticFeedback(1);
                queueEvent(new Runnable() {
                    public void run()
                    {
                        GL2JNILib.undo();
                        RepixActivity.glView.requestRender();
                    }
                });
            }
        });
        menuUndo.getActionView().setOnLongClickListener(new android.view.View.OnLongClickListener() {
            public boolean onLongClick(View view)
            {
                Platform.platformCommand("reset_to_original");
                return true;
            }
        });
        setRedoEnabled(false);
        return true;
    }

    public void onDestroy()
    {
        super.onDestroy();
        PurchaseManager.getInstance().unbind();
    }

    public boolean onKeyUp(int i, KeyEvent keyevent)
    {
        if(i == 82)
        {
            toggleMenu();
            return true;
        } else
        {
            return super.onKeyUp(i, keyevent);
        }
    }

    public void onLowMemory()
    {
        cleanupMemory();
    }

    protected void onNewIntent(Intent intent)
    {
        handleSendImageIntent(intent);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        if(shareInfo != null && shareInfo.containsKey(menuitem))
        {
            ResolveInfo resolveinfo = (ResolveInfo)shareInfo.get(menuitem);
            shareDrawer.performHapticFeedback(1);
            sharePhoto(resolveinfo);
            return true;
        }
        switch(menuitem.getItemId())
        {
        default:
            return false;

        case 16908332: 
            toggleMenu();
            shareDrawer.performHapticFeedback(1);
            return true;

        case 2131099668: 
            Platform.platformCommand("reset_to_original");
            return true;

        case 2131099672: 
            shareDrawer.performHapticFeedback(1);
            sharePhoto(null);
            return true;

        case 2131099674: 
            shareDrawer.performHapticFeedback(1);
            closeStore();
            return true;

        case 2131099669: 
            shareDrawer.performHapticFeedback(1);
            queueEvent(new Runnable() {
                public void run()
                {
                    GL2JNILib.undo();
                    RepixActivity.glView.requestRender();
                }
            });
            return true;

        case 2131099670: 
            shareDrawer.performHapticFeedback(1);
            queueEvent(new Runnable() {
                public void run()
                {
                    GL2JNILib.redo();
                    RepixActivity.glView.requestRender();
                }
            });
            return true;
        }
    }

    protected void onPause()
    {
        glViewHack = true;
        glView.setVisibility(8);
        cancelAutoRestore();
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        glView.onResume();
    }

    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, "8CJS65W526X3FZT7JMFH");
    }

    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public void onTrimMemory(int i)
    {
        cleanupMemory();
    }

    public void onWindowFocusChanged(boolean flag)
    {
        super.onWindowFocusChanged(flag);
        if(flag && glViewHack)
        {
            glViewHack = false;
            glView.setVisibility(0);
        }
    }

    void openCamera()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", Uri.fromFile(getCameraFile()));
        startActivityForResult(intent, 40002);
    }

    public void openEditor()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                setHeadingTitle(R.string.app_name);
                if(currentToolbar != null)
                {
                    setToolbar(currentToolbar);
                }
                shareDrawer.closeMenu();
                menuDrawer.closeMenu();
                shareDrawer.setTouchMode(0);
                menuDrawer.setTouchMode(0);
                webviewlayout.setVisibility(4);
                webview.loadEmpty();
                webview.clearCache(false);
                RepixActivity.glView.setVisibility(0);
                requestRender();
            }
        });
    }

    void openFeed()
    {
        hideToolbarButtons();
        setHeadingTitle(R.string.heading_starters);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        openWeb("http://repix.it/android/feed");
    }

    void openGallery()
    {
        Intent intent = new Intent("android.intent.action.PICK", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 40001);
    }

    void openHelp()
    {
        hideToolbarButtons();
        setHeadingTitle(R.string.heading_help);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        openWeb("http://repix.it/android150/help");
    }

    public void openPhoto(Bitmap bitmap)
    {
        if(bitmap == null);
        pendingBitmap = bitmap;
        exif.reset();
        System.gc();
        queueEvent(new Runnable() {
            public void run()
            {
                GL2JNILib.clear(null, 0, 0);
                openEditor();
            }
        });
    }

    public void openStore()
    {
        queueEvent(new Runnable() {
            public void run()
            {
                openEditor();
                GL2JNILib.openStore();
                RepixActivity.glView.requestRender();
            }
        });
    }

    public void openWeb(String s)
    {
        webview.loadUrl(s);
        shareDrawer.closeMenu();
        menuDrawer.closeMenu();
        shareDrawer.setTouchMode(0);
        menuDrawer.setTouchMode(2);
        webviewlayout.setVisibility(0);
        glView.setVisibility(4);
    }

    public void performHapticFeedback(final int feedbackConstant)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                shareDrawer.performHapticFeedback(feedbackConstant);
            }
        });
    }

    public void queueEvent(Runnable runnable)
    {
        if(glView != null)
        {
            glView.queueEvent(runnable);
        }
    }

    public void requestRender()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                RepixActivity.glView.requestRender();
            }
        });
    }

    void saveExif(Uri uri) throws IOException
    {
        String s;
        s = getContentFilename(uri);
        if(s == null)
        {
            //break MISSING_BLOCK_LABEL_167;
        	return;
        }
        String s1 = null;
        try
        {
        	PackageInfo packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        	if(packageinfo != null)
                s1 = packageinfo.versionName;
        }catch(android.content.pm.PackageManager.NameNotFoundException namenotfoundexception){
            s1 = null;
        }
        if(s1 == null)
        {
            s1 = "0.0";
        }
        exif.setAttribute("DateTime", currentTimeAsExif());
        exif.setAttribute("Orientation", "0");
        exif.setAttribute("Software", (new StringBuilder()).append("Repix ").append(s1).append(" (Android)").toString());
        exif.setAttribute("Description", "Made with Repix (http://repix.it)");
        exif.setAttribute("ImageLength", null);
        exif.setAttribute("ImageWidth", null);
        exif.setAttribute("ImageHeight", null);
        exif.writeExif(s);
    }

    public void setCurrentPhoto(Bitmap bitmap)
    {
        pendingBitmap = bitmap;
        exif.reset();
        requestRender();
    }

    void setHeadingTitle(int i)
    {
        setHeadingTitle(getResources().getString(i));
    }

    void setHeadingTitle(final String title)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                ActionBar actionbar = getActionBar();
                if(actionbar != null)
                {
                    if("Repix".equals(title))
                    {
                        actionbar.setDisplayShowTitleEnabled(false);
                    } else
                    {
                        actionbar.setDisplayShowTitleEnabled(true);
                    }
                    actionbar.setTitle(title);
                }
            }
        });
    }

    public void setRedoEnabled(final boolean state)
    {
        if(state == redoState || menuRedo == null)
        {
            return;
        } else
        {
            redoState = state;
            runOnUiThread(new Runnable() {
                public void run()
                {
                    menuRedo.setEnabled(state);
                }
            });
            return;
        }
    }

    void setStatus(boolean flag)
    {
    }

    public void setToolbar(final String tag)
    {
        currentToolbar = tag;
        if(menuUndo == null)
        {
            return;
        } else
        {
            runOnUiThread(new Runnable() {
                public void run()
                {
                    boolean flag = true;
                    boolean flag1 = tag.equals("brushes");
                    menuUndo.setVisible(flag1);
                    menuRedo.setVisible(flag1);
                    boolean flag2 = tag.startsWith("store");
                    menuStoreClose.setVisible(flag2);
                    MenuItem menuitem = menuReset;
                    boolean flag3;
                    MenuItem menuitem1;
                    if(!flag2)
                    {
                        flag3 = flag;
                    } else
                    {
                        flag3 = false;
                    }
                    menuitem.setVisible(flag3);
                    menuitem1 = menuSave;
                    if(flag2)
                    {
                        flag = false;
                    }
                    menuitem1.setVisible(flag);
                    if(!flag2)
                    {
                        setHeadingTitle(R.string.app_name);
                    }
                }
            });
            return;
        }
    }

    public void sharePhoto(final ResolveInfo info)
    {
        final boolean saveOnly;
        if(info == null || info.activityInfo == null || "it.repix.android".equals(info.activityInfo.packageName))
        {
            saveOnly = true;
        } else
        {
            saveOnly = false;
        }
        if(!Environment.getExternalStorageState().equals("mounted"))
        {
            alert("Cannot save, external storage is not available", null);
            return;
        } else
        {
            queueEvent(new Runnable() {
                public void run()
                {
                    try
                    {
                        final Bitmap bitmap = GL2JNILib.getProcessedPhoto();
                        runOnUiThread(new Runnable() {
                            public void run()
                            {
                                try
                                {
                                	final String s;
                                    s = savePhoto(bitmap);
                                    if(saveOnly)
                                    {
                                        FlurryAgent.logEvent("save:gallery");
                                        Toast.makeText(RepixActivity.this, "Saved to Gallery", 1).show();
                                        return;
                                    }
                                    final ComponentName name = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
                                    Toast.makeText(RepixActivity.this, "Opening share application...", 0).show();
                                    FlurryAgent.logEvent((new StringBuilder()).append("share:").append(name).toString());
                                    (new Handler()).postAtTime(new Runnable() {
                                        public void run()
                                        {
                                            Intent intent = new Intent("android.intent.action.SEND");
                                            intent.setComponent(name);
                                            intent.setType("image/jpeg");
                                            intent.putExtra("android.intent.extra.STREAM", Uri.parse(s));
                                            intent.putExtra("android.intent.extra.SUBJECT", "Made with Repix - http://repix.it ");
                                            intent.putExtra("android.intent.extra.TEXT", "#repix ");
                                            startActivity(intent);
                                        }
                                    }, 50L);
                                    return;
                                }
                                catch(IOException ioexception)
                                {
                                    Toast.makeText(RepixActivity.this, (new StringBuilder()).append("Could not save: ").append(ioexception.getMessage()).toString(), 0).show();
                                }
                                return;
                            }
                        });
                        return;
                    }
                    catch(Exception exception)
                    {
                        return;
                    }
                }
            });
            return;
        }
    }

    void showWhatsNew()
    {
        (new WhatsNewDlg(findViewById(0x1020002).getWidth(), findViewById(0x1020002).getHeight())).show(getFragmentManager(), "WhatsNewDlg");
    }

    void toggleMenu()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                int i = getResources().getDimensionPixelSize(R.dimen.sidemenu_width);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                RepixActivity.getInstance().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int j = Math.min(i, -60 + displaymetrics.widthPixels);
                menuDrawer.setMenuSize(j);
                shareDrawer.closeMenu();
                menuDrawer.toggleMenu();
                setStatus(menuDrawer.isMenuVisible());
            }
        });
    }

    void toggleShareMenu()
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                shareDrawer.toggleMenu();
                menuDrawer.closeMenu();
                setStatus(shareDrawer.isMenuVisible());
            }
        });
    }
}
