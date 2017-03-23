#extension GL_EXT_shader_texture_lod : enable
                    
#ifndef GL_EXT_shader_texture_lod
lowp vec4 texture2DLodEXT(mediump sampler2D t, mediump vec2 c, lowp float level )
{
	return texture2D(t, c, 2.0+level);
}
#endif

uniform sampler2D backbuffer;			// mipmaps
//uniform sampler2D tex1;					// source
uniform mediump vec2 backbufferSize;
uniform mediump float bluramount;
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying mediump vec2 tex1Coord;


void main (void)
{
	// alphamul
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
	
	gl_FragColor = vec4( texture2DLodEXT( backbuffer, tex1Coord, bluramount).rgb, alphamul*vertexColor.a );
}
