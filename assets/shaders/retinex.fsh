#extension GL_EXT_shader_texture_lod : enable
                    
/*
#ifndef GL_EXT_shader_texture_lod
highp vec4 texture2DLodEXT(mediump sampler2D t, mediump vec2 c, lowp float level )
{
	return texture2D(t, c, 2.0+level);
}
#endif
*/

uniform highp sampler2D backbuffer;			// mipmaps
uniform highp sampler2D tex1;					// source
uniform highp vec2 backbufferSize;
uniform highp vec2 mipmapLevel0Size;
uniform highp float thickness;
varying mediump vec4 vertexColor;
varying highp vec2 texCoord;
varying highp vec2 tex1Coord;


highp vec4 sampleBlurmap( mediump sampler2D bm, highp vec2 c, mediump float level ) {
	level = floor(level);
	highp vec2 size = (mipmapLevel0Size / clamp( pow(2.0, level), 1.0, 100.0 ));
	c = c*size;
	highp vec2 topleft = floor( c );
	highp vec2 bottomright = topleft + vec2(1.0, 1.0); //ceil(c);
	c = fract(c);
	topleft /= size;
	bottomright /= size;
	return mix(texture2DLodEXT( bm, topleft, level ),texture2DLodEXT( bm, vec2(bottomright.x, topleft.y), level ), c.x) * (1.0-c.y) + 
		   mix(texture2DLodEXT( bm, vec2(topleft.x, bottomright.y), level ),texture2DLodEXT( bm, bottomright, level ),c.x) * (c.y);
}




void main (void)
{

	highp vec3 ocol = texture2D( tex1, tex1Coord ).rgb;
	highp vec3 l2col = sampleBlurmap( backbuffer, tex1Coord, thickness ).rgb;

/*
	
	highp vec3 ocol = sampleBlurmap( backbuffer, tex1Coord, thickness ).rgb;
	highp vec3 l2col = sampleBlurmap( backbuffer, tex1Coord, thickness + 1.0 ).rgb;
	*/
	
	/*
		// Difference of gaussians
	highp vec3 dif = (sampleBlurmap( backbuffer, tex1Coord, thickness+1.0 ).rgb) - 
					 (sampleBlurmap( backbuffer, tex1Coord, thickness ).rgb);
	
	gl_FragColor = vec4(vec3(0.5, 0.5, 0.5) + dif*5.0, 1.0 );
	*/
			// alphamul
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	highp float alphamul = clamp( 1.0-( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);

	gl_FragColor = vec4( ocol/l2col, alphamul * vertexColor.a );
	
}
