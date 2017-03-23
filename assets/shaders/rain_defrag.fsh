					uniform sampler2D sampler2d;
					varying mediump vec4 vertexColor;
					varying mediump vec2 texCoord;
					varying mediump vec2 tex1Coord;
					varying mediump mat4 thisim;
					uniform sampler2D backbuffer;
					varying highp vec2 backbufferCoord;
					uniform highp vec2 backbufferSize;
					uniform highp float loopsize;
					const highp vec3 normalmiddle =  vec3( 0.74117647, 0.74117647, 0.5 );
					uniform mediump vec2 lightvector;
					
					void main (void)
					{
						highp vec2 tc = vec2( tex1Coord.x, tex1Coord.y/backbufferSize.x*backbufferSize.y) * loopsize;
						highp vec4 v = texture2D( sampler2d, tc );
					
						highp vec3 normal = normalize( v.rgb - normalmiddle );
						
						highp float alpha = v.a;
						highp float amount = 0.15;
						
					

						
							// initialize bounce as "toviewer"
						tc = backbufferCoord.xy - vec2(0.5, 0.5);
						
						highp vec3 bouncevec = normalize( vec3(tc, 1.0) );
						bouncevec = normalize(bouncevec-vec3(normal.xy, 0.0) * amount);
						// bounce is now outgoing. 
						
						tc = bouncevec.xy / (bouncevec.z);
	
						v = texture2D( backbuffer, vec2(0.5, 0.5) + tc );
						
						//v = texture2D( backbuffer, vec2(0.5, 0.5) + (bouncevec.xy/bouncevec.z) );

						//v = texture2D( backbuffer, vec2(0.5, 0.5) + bouncevec.xy );
						
						//v = texture2D( backbuffer, backbufferCoord + normal.xy*amount );
						
						tc = (texCoord - vec2(0.5, 0.5))*2.0;
						alpha *= clamp( 1.0-dot(tc,tc), 0.0, 1.0);
						gl_FragColor = vec4(v.rgb, alpha );
                    }
