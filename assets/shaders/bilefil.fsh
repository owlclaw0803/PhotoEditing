uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
uniform mediump vec2 backbufferSize;

varying mediump vec4 vertexColor;
varying highp vec2 texCoord;
varying highp vec2 backbufferCoord;


void main (void)
{
	mediump vec3 bg = texture2D( backbuffer, backbufferCoord).rgb;

	const mediump int blur_samples = 2;
	mediump vec2 blur_r = vec2( 1.0/backbufferSize.x, 1.0/backbufferSize.y );

	mediump vec3 c;
	mediump vec3 tempv;
	highp vec3 collected_color = vec3(0.0,0.0,0.0);
	highp float totalpower = 0.0;
	
	for (int y=-blur_samples; y<=blur_samples; y++)
	{
		for (int x=-blur_samples; x<=blur_samples; x++) 
		{
			mediump float dpower = float(2*blur_samples*blur_samples-(x*x+y*y));
			if (dpower>0.0) {
			
				c = texture2D( backbuffer, backbufferCoord + (vec2(x,y) * blur_r)).rgb;
				tempv = (c-bg);
				dpower *= clamp( 1.0-(dot(tempv, tempv)), 0.0, 1.0 );
				
				collected_color = collected_color + c*dpower;
				totalpower = totalpower + dpower;
			}
		}
	}
	collected_color = collected_color / totalpower;
	gl_FragColor = vec4(collected_color, vertexColor.a);
	
	//gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0 );
}

