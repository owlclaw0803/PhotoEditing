uniform sampler2D sampler2d;
uniform sampler2D tex1;
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying highp vec2 tex1Coord;
uniform highp vec2 backbufferSize;
uniform sampler2D backbuffer;
varying mediump vec2 backbufferCoord;
uniform mediump float opacity;

#ifdef GL_EXT_shader_framebuffer_fetch
#extension GL_EXT_shader_framebuffer_fetch : enable
#define CUSTOM_BLEND 1
#endif

#ifdef CUSTOM_BLEND
mediump vec4 current()
{
    return gl_LastFragData[0];
}
#else
mediump vec4 current()
{
    return texture2D(backbuffer, backbufferCoord);
}
#endif



void main (void)
{
    mediump vec4 t0 = texture2D(sampler2d, texCoord);
    
    mediump vec3 curr = current().rgb;
    mediump vec3 orig = texture2D(tex1, tex1Coord).rgb;
    
    mediump vec3 diff = curr.rgb - orig;
    mediump float dm = ( dot(diff, diff ) );

#ifdef CUSTOM_BLEND
    mediump float alpha = clamp((opacity * vertexColor.a * t0.a * (1.0+0.05/sqrt(dm))), 0.0, 1.0);
    gl_FragColor = vec4( mix(curr, orig, alpha),  1.0 );
#else
    //mediump float alpha = clamp((opacity * vertexColor.a * t0.a * (3.0+0.05/sqrt(dm))), 0.0, 1.0);
    mediump float alpha = clamp((opacity * vertexColor.a * t0.a * (0.5+0.05/sqrt(dm))), 0.0, 1.0);
    gl_FragColor = vec4( orig, alpha );
#endif
}
