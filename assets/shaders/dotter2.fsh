#extension GL_EXT_shader_texture_lod : enable

#ifndef GL_EXT_shader_texture_lod
highp vec4 texture2DLodEXT(mediump sampler2D t, mediump vec2 c, lowp float level )
{
	return texture2D(t, c, 3.0+level);
}
#endif


uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
uniform mediump vec2 backbufferSize;
uniform mediump vec3 useColors[4];
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying mediump vec2 tex1Coord;
uniform highp float loopsize;
//const mediump float loopsize = 50.0;
const highp vec2 mul15deg=vec2(0.96592582,0.25881904);
const highp vec2 mul45deg=vec2(0.70710678,0.70710678);

highp float pattern(highp vec2 p)
{
	return clamp(1.3*distance(fract(p), vec2(0.5,0.5)), 0.1, 1.0);
}

highp float mixmul( highp vec3 curcol, highp vec3 newcol, highp vec3 targetcol ) {
	highp float div = ( (newcol.r-curcol.r) + (newcol.g-curcol.g) + (newcol.b-curcol.b) );
	highp vec3 m = (targetcol-curcol) / div;
	return clamp( (m.x+m.y+m.z), 0.0, 1.0);
}

highp float sclamp(highp float x, highp float a, highp float b)
{
    return smoothstep(a, b, x)*(b-a)+a;
}

void main (void)
{
	mediump float level = clamp( (50.0-loopsize)/20.0+1.1, 1.1, 5.0 );
	highp vec3 bg = texture2DLodEXT(backbuffer, tex1Coord,level).rgb;
	
	
	highp vec4 comp;
	
	
	
	comp.b = mixmul( useColors[0], useColors[1], bg );
	highp vec3 tecol = mix( useColors[0], useColors[1], comp.b );

	comp.g = mixmul(tecol, useColors[2], bg );
	tecol = mix( tecol, useColors[2], comp.g );

	comp.r = mixmul(tecol, useColors[3], bg );
	//tecol = mix( tecol, useColors[3], comp.r );			// unrequired

	
    
    
    highp vec2 tc = vec2( tex1Coord.x, tex1Coord.y*(backbufferSize.y/backbufferSize.x)) * loopsize;
	
    /*
    highp vec4 rasterSample = vec4( pattern(vec2( tc.x * mul15deg.x + tc.y * mul15deg.y,tc.x * mul15deg.y + tc.y * -mul15deg.x )),
									  pattern(vec2( tc.x * mul15deg.y + tc.y * mul15deg.x,tc.x * mul15deg.x + tc.y * -mul15deg.y )),
									  pattern(vec2( tc.x * mul45deg.x + tc.y * mul45deg.y, tc.x * mul45deg.y + tc.y * -mul45deg.x )),
									  pattern(tc ) );
													

    //comp.g = 0.1;
    
	//comp = clamp( vec4(0.5, 0.5, 0.5, 0.5) + (comp-rasterSample)*10.0, 0.0, 1.0 );
	comp = clamp( (comp-rasterSample)*10.0, 0.0, 1.0 );
	
    
    tecol = mix( useColors[0], useColors[1], comp.b );
    tecol = clamp( tecol, 0.0, 1.0);
    tecol = mix( tecol, useColors[2], comp.g );
    tecol = clamp( tecol, 0.0, 1.0);
    tecol = mix( tecol, useColors[3], comp.r );
    tecol = clamp( tecol, 0.0, 1.0);
    */
    
    
	
	tecol = mix( useColors[0], useColors[1], sclamp((comp.b-pattern(vec2( tc.x * mul45deg.x + tc.y * mul45deg.y, tc.x * mul45deg.y + tc.y * -mul45deg.x )))*10.0, 0.0, 1.0 ) );

    tecol = clamp( tecol, 0.0, 1.0);
   
    mediump float ftemp=sclamp((comp.g-pattern(vec2( tc.x * mul15deg.y + tc.y * mul15deg.x,tc.x * mul15deg.x + tc.y * -mul15deg.y )))*10.0, 0.0, 1.0 );
    
    tecol = tecol*(1.0-ftemp) + useColors[2]*ftemp;
    tecol = clamp(tecol,0.0, 1.0);
   
    ftemp = sclamp((comp.r-pattern(vec2( tc.x * mul45deg.x + tc.y * mul45deg.y, tc.x * mul45deg.y + tc.y * -mul45deg.x )))*10.0, 0.0, 1.0 );
    
    tecol = tecol*(1.0-ftemp) + useColors[3]*ftemp;
    // tecol = clamp( tecol, 0.0, 1.0);


	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
	gl_FragColor = vec4( tecol, alphamul );
}