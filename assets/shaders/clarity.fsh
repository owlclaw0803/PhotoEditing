uniform sampler2D sampler2d;
uniform sampler2D backbuffer;

uniform mediump float clarity_mul;
varying mediump vec4 vertexColor;
varying highp vec2 texCoord;
varying highp vec2 backbufferCoord;

mediump vec4 clarity()
{
	const mediump int blur_samples = 2;
	mediump float blur_r = 0.00133;
	mediump vec4 c;
	
	mediump vec4 collected_color = vec4(0,0,0,0);
	mediump float totalpower = 0.0;
	for (int y=-blur_samples; y!=blur_samples+1; y++)
		for (int x=-blur_samples; x!=blur_samples+1; x++) 
		{
			mediump float dpower = float(2*blur_samples*blur_samples-(x*x+y*y));
			c = texture2D( backbuffer, backbufferCoord + (vec2(x,y) * blur_r)) * dpower;
			collected_color = collected_color + c;
			totalpower = totalpower + dpower;
		}
	//						collected_color = collected_color / totalpower;
	collected_color = collected_color / 100.0;  // for blur_samples=2
	
	c = texture2D( backbuffer, backbufferCoord);
	c += (c - collected_color) * clarity_mul;
	                        
	return c;

}

                    

void main (void)
{
	lowp vec4 t0 = texture2D(sampler2d, texCoord);
	lowp vec4 c = clarity(); //texture2D( backbuffer, backbufferCoord);
	gl_FragColor = vec4(c.rgb, t0.a*vertexColor.a);
}

