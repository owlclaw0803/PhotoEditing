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


const float ca = 0.9987502603949663;
const float sa = 0.04997916927067833;

const mat2 m = mat2(ca, -sa, sa, ca);

void main (void)
{
	// alphamul
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
	
    mediump float amount = distance(tex1Coord, vec2(0.5))*2.0*bluramount;

	vec3 c = texture2D( tex1, tex1Coord).rgb;
    
    float a = 0.03;
	
    mediump vec2 s = (tex1Coord-vec2(0.5))*m+vec2(0.5);
    
    for(int i=0;i<8;i++)
    {
	c = mix(c, texture2D(backbuffer, s).rgb, 0.025*(9.0-float(i)));
	s = (s-vec2(0.5))*m+vec2(0.5);
	}

	gl_FragColor = vec4(c, alphamul*vertexColor.a);
}
