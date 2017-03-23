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
uniform mediump float level;
uniform highp float tresshold;
uniform lowp vec3 useColors[4];


mediump float cerror( mediump vec3 c1, mediump vec3 c2 ) 
{
	mediump vec3 d = c2-c1;
	return dot(d,d);
}



void main (void)
{
	mediump vec3 bg = texture2D(backbuffer, tex1Coord, 3.0).rgb;
    
	lowp vec3 current = useColors[0];
	mediump float error = cerror(current, bg);

	for (int f=1; f<4; f++) {
		mediump float e = cerror(useColors[f], bg);
		if (e<error) {
            current = mix(current, useColors[f], clamp(3.0*((error-e)/error), 0.0, 1.0));
			error = e;
		}
	}
    
	mediump vec2 tc = (texCoord - vec2(0.5, 0.5))*2.0;
	mediump float alpha = clamp( 1.0-dot(tc,tc), 0.0, 1.0 );
	gl_FragColor=vec4(current, vertexColor.a*alpha);
}
