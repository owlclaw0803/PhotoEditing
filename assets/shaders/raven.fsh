uniform highp sampler2D sampler2d;
varying lowp vec4 vertexColor;
varying highp vec2 texCoord;
void main (void)
{
	gl_FragColor = texture2D(sampler2d, texCoord)*vertexColor;
}