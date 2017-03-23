					uniform sampler2D sampler2d;
					uniform sampler2D tex1;
					uniform sampler2D backbuffer;
					varying mediump vec4 vertexColor;
					varying highp vec2 texCoord;
					varying highp vec2 tex1Coord;
					varying highp vec2 backbufferCoord;
uniform mediump float brightness;

mediump vec4 lut3d(highp vec4 textureColor)
{
     mediump float blueColor = textureColor.b * 63.0;

     mediump vec2 quad1;
     quad1.y = floor(floor(blueColor) / 8.0);
     quad1.x = floor(blueColor) - (quad1.y * 8.0);
     
     mediump vec2 quad2;
     quad2.y = floor(ceil(blueColor) / 8.0);
     quad2.x = ceil(blueColor) - (quad2.y * 8.0);
     
     highp vec2 texPos1;
     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);
     
     highp vec2 texPos2;
     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);
     
     mediump vec4 newColor1 = texture2D(sampler2d, texPos1);
     mediump vec4 newColor2 = texture2D(sampler2d, texPos2);
     
     //mediump vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
     //return newColor;
     
     mediump float a = clamp( fract(blueColor), 0.0, 1.0);
	 return newColor1*(1.0-a) + newColor2*(a);
}

void main (void)
{
    lowp vec4 t0 = texture2D(sampler2d, texCoord);
    lowp vec4 t2 = texture2D(backbuffer, backbufferCoord);
    mediump float a = clamp( (1.0-2.0*distance(texCoord, vec2(0.5,0.5))) * vertexColor.a, 0.0, 1.0);
    mediump vec3 ncol = lut3d(t2).rgb + vec3(brightness, brightness, brightness);
    gl_FragColor = vec4( ncol, a );
    //gl_FragColor = vec4(lut3d(t2).rgb+vec3(brightness,brightness,brightness), a*vertexColor.a);
}

