uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
uniform mediump vec2 backbufferSize;
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying mediump vec2 tex1Coord;
//uniform highp vec2 samplingUnit;
uniform highp float thickness;			// original was 1.5

void main (void)
{
	highp vec2 samplingUnit = vec2( thickness/backbufferSize.x, thickness/backbufferSize.y );
	
	highp vec4 topleft = texture2D(backbuffer, tex1Coord + vec2(-samplingUnit.x, -samplingUnit.y));
	highp vec4 topmiddle = texture2D(backbuffer, tex1Coord + vec2(0.0, -samplingUnit.y));
	highp vec4 topright = texture2D(backbuffer, tex1Coord + vec2(samplingUnit.x, -samplingUnit.y));
	highp vec4 middleleft = texture2D(backbuffer, tex1Coord + vec2(-samplingUnit.x, 0.0));
	highp vec4 middleright = texture2D(backbuffer, tex1Coord + vec2(samplingUnit.x, 0.0));
	highp vec4 bottomleft = texture2D(backbuffer, tex1Coord + vec2(-samplingUnit.x, samplingUnit.y));
	highp vec4 bottommiddle = texture2D(backbuffer, tex1Coord + vec2(0.0, samplingUnit.y));
	highp vec4 bottomright = texture2D(backbuffer, tex1Coord + vec2(samplingUnit.x, samplingUnit.y));

	
	// calculate sobel out of it
	highp vec4 gX = -topleft + topright + -2.0*middleleft + 2.0*middleright + -bottomleft + bottomright;
	highp vec4 gY = -topleft + -2.0*topmiddle -topleft + bottomleft + 2.0*bottommiddle + bottomright;

	//mediump float gradpower = sqrt( dot(gX,gX) + dot(gY,gY) );
	highp float gradpower = (dot(gX,gX) + dot(gY,gY)) / (2.5+thickness*0.25);
	gradpower = clamp( pow(gradpower, 0.2+thickness*0.25),0.0,1.0);
							
	topleft = texture2D( sampler2d, texCoord );
	//gl_FragColor = vec4(vertexColor.rgb,gradpower*topleft.a*vertexColor.a);
	gl_FragColor = vec4(0,0,0,gradpower*topleft.a*vertexColor.a);
}