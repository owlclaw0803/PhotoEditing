precision mediump float;

#extension GL_EXT_shader_texture_lod : enable
                    
#ifndef GL_EXT_shader_texture_lod
lowp vec4 texture2DLodEXT(mediump sampler2D t, mediump vec2 c, lowp float level )
{
	return texture2D(t, c, 2.0+level);
}
#endif

uniform sampler2D backbuffer;			// mipmaps
uniform sampler2D tex1;					// source
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
	
    mediump float amount = distance(tex1Coord, vec2(0.5))*2.0*bluramount;
    
    vec3 blurred = texture2DLodEXT( backbuffer, tex1Coord, amount-1.0).rgb;
    vec3 texture = texture2D( tex1, tex1Coord, 0.0).rgb;
    
	gl_FragColor = vec4(mix(texture, blurred, clamp(amount, 0.0, 1.0)), alphamul*vertexColor.a);
}
