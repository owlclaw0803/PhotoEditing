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
//uniform mediump float level;
uniform mediump float power;

uniform lowp vec3 useColors[4];

const mediump float level=1.0;

	// power was 4
highp float dpower( mediump vec3 c1, mediump vec3 c2, mediump float power ) 
{
	c1 = (c2-c1);
	return 1.0/(pow( dot(c1,c1), power) +0.00001);
}

void main (void)
{
	mediump vec3 bg = texture2DLodEXT(backbuffer, tex1Coord,level).rgb;
	// choose one of the useColors according bg

	highp vec4 powers;
	powers.r = dpower( bg, useColors[0],power );
	powers.g = dpower( bg, useColors[1],power );
	powers.b = dpower( bg, useColors[2],power );
	powers.a = dpower( bg, useColors[3],power );
	highp float totalp = powers.r + powers.g + powers.b + powers.a;
	
	highp vec3 finalc = useColors[0]*powers.r/totalp + useColors[1]*powers.g/totalp +
						useColors[2]*powers.b/totalp + useColors[3]*powers.a/totalp;
	
	
	
	//mediump float m = clamp( tresshold-sqrt( dot( bg, bg ) ) * 30.0, 0.0, 1.0);
	mediump vec2 tc = (texCoord - vec2(0.5, 0.5))*2.0;
	mediump float alpha = clamp( 1.0-dot(tc,tc), 0.0, 1.0 );
	
	mediump float alphamul = vertexColor.a*alpha;
	gl_FragColor=vec4( finalc, alphamul );

}
