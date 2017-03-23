package com.android.vending.billing;

import android.os.*;

public interface IInAppBillingService
    extends IInterface
{
    public static abstract class Stub extends Binder
        implements IInAppBillingService
    {

        private static final String DESCRIPTOR = "com.android.vending.billing.IInAppBillingService";
        static final int TRANSACTION_consumePurchase = 5;
        static final int TRANSACTION_getBuyIntent = 3;
        static final int TRANSACTION_getPurchases = 4;
        static final int TRANSACTION_getSkuDetails = 2;
        static final int TRANSACTION_isBillingSupported = 1;

        public static IInAppBillingService asInterface(IBinder ibinder)
        {
            if(ibinder == null)
            {
                return null;
            }
            IInterface iinterface = ibinder.queryLocalInterface("com.android.vending.billing.IInAppBillingService");
            if(iinterface != null && (iinterface instanceof IInAppBillingService))
            {
                return (IInAppBillingService)iinterface;
            } else
            {
                return new Proxy(ibinder);
            }
        }

        public IBinder asBinder()
        {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel1, int j)
            throws RemoteException
        {
            switch(i)
            {
            default:
                return super.onTransact(i, parcel, parcel1, j);

            case 1598968902: 
                parcel1.writeString("com.android.vending.billing.IInAppBillingService");
                return true;

            case 1: // '\001'
                parcel.enforceInterface("com.android.vending.billing.IInAppBillingService");
                int i1 = isBillingSupported(parcel.readInt(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                parcel1.writeInt(i1);
                return true;

            case 2: // '\002'
                parcel.enforceInterface("com.android.vending.billing.IInAppBillingService");
                int l = parcel.readInt();
                String s = parcel.readString();
                String s1 = parcel.readString();
                Bundle bundle2;
                Bundle bundle3;
                if(parcel.readInt() != 0)
                {
                    bundle2 = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                } else
                {
                    bundle2 = null;
                }
                bundle3 = getSkuDetails(l, s, s1, bundle2);
                parcel1.writeNoException();
                if(bundle3 != null)
                {
                    parcel1.writeInt(1);
                    bundle3.writeToParcel(parcel1, 1);
                } else
                {
                    parcel1.writeInt(0);
                }
                return true;

            case 3: // '\003'
                parcel.enforceInterface("com.android.vending.billing.IInAppBillingService");
                Bundle bundle1 = getBuyIntent(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                if(bundle1 != null)
                {
                    parcel1.writeInt(1);
                    bundle1.writeToParcel(parcel1, 1);
                } else
                {
                    parcel1.writeInt(0);
                }
                return true;

            case 4: // '\004'
                parcel.enforceInterface("com.android.vending.billing.IInAppBillingService");
                Bundle bundle = getPurchases(parcel.readInt(), parcel.readString(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                if(bundle != null)
                {
                    parcel1.writeInt(1);
                    bundle.writeToParcel(parcel1, 1);
                } else
                {
                    parcel1.writeInt(0);
                }
                return true;

            case 5: // '\005'
                parcel.enforceInterface("com.android.vending.billing.IInAppBillingService");
                int k = consumePurchase(parcel.readInt(), parcel.readString(), parcel.readString());
                parcel1.writeNoException();
                parcel1.writeInt(k);
                return true;
            }
        }

        public Stub()
        {
            attachInterface(this, "com.android.vending.billing.IInAppBillingService");
        }
    }

    public static class Proxy implements IInAppBillingService
    {
        private IBinder mRemote;

        public IBinder asBinder()
        {
            return mRemote;
        }

        public int consumePurchase(int i, String s, String s1)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
            parcel.writeInt(i);
            parcel.writeString(s);
            parcel.writeString(s1);
            mRemote.transact(5, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
        }

        public Bundle getBuyIntent(int i, String s, String s1, String s2, String s3)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
            parcel.writeInt(i);
            parcel.writeString(s);
            parcel.writeString(s1);
            parcel.writeString(s2);
            parcel.writeString(s3);
            mRemote.transact(3, parcel, parcel1, 0);
            parcel1.readException();
            Bundle bundle;
            if(parcel1.readInt() == 0) 
            	bundle = null;
            else
            	bundle = (Bundle)Bundle.CREATOR.createFromParcel(parcel1);
            parcel1.recycle();
            parcel.recycle();
            return bundle;
        }

        public String getInterfaceDescriptor()
        {
            return "com.android.vending.billing.IInAppBillingService";
        }

        public Bundle getPurchases(int i, String s, String s1, String s2)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
            parcel.writeInt(i);
            parcel.writeString(s);
            parcel.writeString(s1);
            parcel.writeString(s2);
            mRemote.transact(4, parcel, parcel1, 0);
            parcel1.readException();
            Bundle bundle;
            if(parcel1.readInt() == 0)
            	bundle = null;
            else
            	bundle = (Bundle)Bundle.CREATOR.createFromParcel(parcel1);
            parcel1.recycle();
            parcel.recycle();
            return bundle;
        }

        public Bundle getSkuDetails(int i, String s, String s1, Bundle bundle)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            parcel.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
            parcel.writeInt(i);
            parcel.writeString(s);
            parcel.writeString(s1);
            if(bundle == null)
            	parcel.writeInt(0);
            else{
            	parcel.writeInt(1);
            	bundle.writeToParcel(parcel, 0);
            }
            Bundle bundle1;
            mRemote.transact(2, parcel, parcel1, 0);
            parcel1.readException();
            if(parcel1.readInt() == 0)
            {
            	bundle1 = null;
            }else
            	bundle1 = (Bundle)Bundle.CREATOR.createFromParcel(parcel1);
            parcel1.recycle();
            parcel.recycle();
            return bundle1;
        }

        public int isBillingSupported(int i, String s, String s1)
            throws RemoteException
        {
            Parcel parcel;
            Parcel parcel1;
            parcel = Parcel.obtain();
            parcel1 = Parcel.obtain();
            int j;
            parcel.writeInterfaceToken("com.android.vending.billing.IInAppBillingService");
            parcel.writeInt(i);
            parcel.writeString(s);
            parcel.writeString(s1);
            mRemote.transact(1, parcel, parcel1, 0);
            parcel1.readException();
            j = parcel1.readInt();
            parcel1.recycle();
            parcel.recycle();
            return j;
        }

        Proxy(IBinder ibinder)
        {
            mRemote = ibinder;
        }
    }


    public abstract int consumePurchase(int i, String s, String s1)
        throws RemoteException;

    public abstract Bundle getBuyIntent(int i, String s, String s1, String s2, String s3)
        throws RemoteException;

    public abstract Bundle getPurchases(int i, String s, String s1, String s2)
        throws RemoteException;

    public abstract Bundle getSkuDetails(int i, String s, String s1, Bundle bundle)
        throws RemoteException;

    public abstract int isBillingSupported(int i, String s, String s1)
        throws RemoteException;
}
