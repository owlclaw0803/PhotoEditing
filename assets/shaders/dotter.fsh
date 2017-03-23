#extension GL_EXT_shader_texture_lod : enable

#ifndef GL_EXT_shader_texture_lod
lowp vec4 texture2DLodEXT(mediump sampler2D t, mediump vec2 c, lowp float level )
{
	return texture2D(t, c, 3.0+level);
}
#endif


uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
uniform mediump vec2 backbufferSize;
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying mediump vec2 tex1Coord;
uniform highp float loopsize;
//const mediump float loopsize = 50.0;
const mediump vec2 mul15deg=vec2(0.96592582,0.25881904);
const mediump vec2 mul45deg=vec2(0.70710678,0.70710678);

mediump float pattern(mediump vec2 p)
{
	return clamp(1.3*distance(fract(p), vec2(0.5,0.5)), 0.1, 1.0);
}


void main (void)
{
#ifndef GL_EXT_shader_texture_lod
    // adreno renders half screen distorted if mipmaplevel is from variable.
    // since the mipmaps arent that exact without the extension, just use fixed one.
    // i love this stuff.
    mediump float level = 0.0;
#else
	mediump float level = clamp( (50.0-loopsize)/20.0+1.1, 1.1, 5.0 );
#endif
    
    
    mediump float loopsizeQuantized = ceil(loopsize/8.0)*8.0;
    
	mediump vec3 bg = texture2DLodEXT(backbuffer, tex1Coord,level).rgb;
	// Convert to CMYK
	mediump vec4 cmyk;
	cmyk.w = min( min( 1.0-bg.r, 1.0-bg.g), 1.0-bg.b );
	cmyk.x = (1.0 - bg.r - cmyk.w) / (1.0-cmyk.w);
	cmyk.y = (1.0 - bg.g - cmyk.w) / (1.0-cmyk.w);
	cmyk.z = (1.0 - bg.b - cmyk.w) / (1.0-cmyk.w);
	
	// contrast?
	cmyk = (cmyk - vec4(0.5,0.5,0.5,0.5)) *1.1 + vec4(0.5,0.5,0.5,0.5);
	
//	mediump vec2 tc = vec2( tex1Coord.x, tex1Coord.y*(backbufferSize.y/backbufferSize.x)) * loopsize;
    mediump vec2 tc = gl_FragCoord.xy / vec2(2048.0) * loopsizeQuantized;
	mediump vec4 rasterSample = vec4( pattern(vec2( tc.x * mul15deg.x + tc.y * mul15deg.y,tc.x * mul15deg.y + tc.y * -mul15deg.x )),
									  pattern(vec2( tc.x * mul15deg.y + tc.y * mul15deg.x,tc.x * mul15deg.x + tc.y * -mul15deg.y )),
									  pattern(tc ),
									  pattern(vec2( tc.x * mul45deg.x + tc.y * mul45deg.y, tc.x * mul45deg.y + tc.y * -mul45deg.x )));
													
													
	
	cmyk = smoothstep(0.0, 17.0, vec4(0.5, 0.5, 0.5, 0.5) + (cmyk-rasterSample)*15.0)*17.0;
	mediump float printmul = clamp( ((cmyk.r + cmyk.g + cmyk.b + cmyk.a)-1.0)*0.5, 0.0, 1.0 );
	

	cmyk = clamp( cmyk, 0.0, 1.0);
	
	// back to rgb
	bg.r = 1.0 - min(1.0,cmyk.x * (1.0-cmyk.w) + cmyk.w);
	bg.g = 1.0 - min(1.0,cmyk.y * (1.0-cmyk.w) + cmyk.w);
	bg.b = 1.0 - min(1.0,cmyk.z * (1.0-cmyk.w) + cmyk.w);
	
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
	gl_FragColor = vec4( bg, vertexColor.a * alphamul * printmul);

}