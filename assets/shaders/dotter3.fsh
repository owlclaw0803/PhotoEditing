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
uniform mediump vec3 useColors[4];
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


highp float dpower( mediump vec3 c1, mediump vec3 c2 ) 
{
	c1 = (c2-c1);
	return 4.0/(dot(c1,c1)*2.0 +0.1);
}


void main (void)
{
	mediump float level = clamp( (50.0-loopsize)/20.0+1.1, 1.1, 5.0 );
	mediump vec3 bg = texture2DLodEXT(backbuffer, tex1Coord,level).rgb;
	
	
	mediump vec4 comp;
	
	highp vec4 powers;
	powers.r = dpower( bg, useColors[0] );
	powers.g = dpower( bg, useColors[1] );
	powers.b = dpower( bg, useColors[2] );
	powers.a = dpower( bg, useColors[3] );
	highp float totalp = powers.r + powers.g + powers.b + powers.a;

	
	powers.r /= totalp;
	powers.g /= totalp;
	powers.b /= totalp;
	powers.a /= totalp;
	
	mediump vec2 tc = vec2( tex1Coord.x, tex1Coord.y*(backbufferSize.y/backbufferSize.x)) * loopsize;
	mediump vec4 rasterSample = vec4( pattern(vec2( tc.x * mul15deg.x + tc.y * mul15deg.y,tc.x * mul15deg.y + tc.y * -mul15deg.x )),
									  pattern(vec2( tc.x * mul15deg.y + tc.y * mul15deg.x,tc.x * mul15deg.x + tc.y * -mul15deg.y )),
									  pattern(tc ),
									  pattern(vec2( tc.x * mul45deg.x + tc.y * mul45deg.y, tc.x * mul45deg.y + tc.y * -mul45deg.x )));
													
													
	 
	powers = clamp( vec4(0.5, 0.5, 0.5, 0.5) + (powers-rasterSample)*30.0, 0.0, 20.0 );
	powers = clamp( powers, 0.0, 1.0);
	
	
	highp vec3 finalc = useColors[0]*powers.r + useColors[1]*powers.g +
						useColors[2]*powers.b + useColors[3]*powers.a;
	
	
	
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
		
	//gl_FragColor = vec4( bg, vertexColor.a * alphamul * printmul);
	gl_FragColor = vec4( finalc, alphamul );

}