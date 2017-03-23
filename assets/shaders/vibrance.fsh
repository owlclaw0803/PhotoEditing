uniform mediump float saturation;

uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
varying mediump vec4 vertexColor;
varying highp vec2 texCoord;
varying highp vec2 backbufferCoord;

void main (void)
{
    mediump vec4 t0 = texture2D(sampler2d, texCoord);
    mediump vec4 t1 = texture2D(backbuffer, backbufferCoord);
    mediump vec3 bw = vec3(dot(t1.rgb, vec3(0.299,0.587,0.114)));
    
    mediump vec3 c = mix(bw, t1.rgb, clamp(saturation, 0.0, 1.05));
    c=mix(c, bw, -distance(c, bw)*2.0*clamp(saturation-1.0, 0.0, 1.0));
    
    gl_FragColor = vec4(c,t0.a*vertexColor.a);
}
