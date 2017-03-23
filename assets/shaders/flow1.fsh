#extension GL_EXT_shader_texture_lod : enable
                    
uniform highp sampler2D backbuffer;			// mipmaps
uniform highp sampler2D tex1;					// source
uniform highp vec2 backbufferSize;
uniform highp vec2 mipmapLevel0Size;
uniform highp float mmlevel;
varying mediump vec4 vertexColor;
varying highp vec2 texCoord;
varying highp vec2 tex1Coord;


mediump float level;
highp vec2 size;


mediump vec4 sampleBlurmap( highp vec2 c ) {
	c = c*size;
	highp vec2 topleft = floor( c );
	highp vec2 bottomright = topleft + vec2(1.0, 1.0); //ceil(c);
	c = fract(c);
	topleft /= size;
	bottomright /= size;
	return mix(texture2DLodEXT( backbuffer, topleft, level ),texture2DLodEXT( backbuffer, vec2(bottomright.x, topleft.y), level ), c.x) * (1.0-c.y) + 
		   mix(texture2DLodEXT( backbuffer, vec2(topleft.x, bottomright.y), level ),texture2DLodEXT( backbuffer, bottomright, level ),c.x) * (c.y);
}


void main (void)
{	
	level = floor(mmlevel);
	size = (mipmapLevel0Size / clamp( pow(2.0, level), 1.0, 100.0 ));
	
	highp vec2 d = vec2( 1.0/size.x, 1.0/size.y);

	mediump vec4 topleft = sampleBlurmap( tex1Coord - d );
	mediump vec4 middleleft = sampleBlurmap( tex1Coord + vec2(-d.x, 0.0 ) );
	mediump vec4 bottomleft = sampleBlurmap( tex1Coord + vec2(-d.x, d.y ) );
	mediump vec4 topright = sampleBlurmap( tex1Coord + vec2( d.x, -d.y ) );
	mediump vec4 middleright = sampleBlurmap( tex1Coord + vec2(d.x, 0.0 ) );
	mediump vec4 bottomright = sampleBlurmap( tex1Coord + d );
	mediump vec4 topmiddle = sampleBlurmap( tex1Coord + vec2( 0.0, -d.y ) );
	mediump vec4 bottommiddle = sampleBlurmap( tex1Coord + vec2( 0.0, d.y ) );
	
	mediump vec4 u = (topleft * -1.0 + middleleft * -2.0 + bottomleft * -1.0 + 
					  topright * 1.0 + middleright * 2.0 + bottomright * 1.0) / 8.0;
					  
    mediump vec4 v = (topleft * -1.0 + topmiddle * -2.0 + topright * -1.0 + 
     				  bottomleft * 1.0 + bottommiddle * 2.0 + bottomright * 1.0 ) / 8.0;



	highp float gradpower = sqrt( dot(u,u) + dot(v,v) );
	highp vec2 dir = vec2( u.r+u.g+u.b, v.r+v.g+v.b );
	//gl_FragColor = vec4( normalize(dir), gradpower, 1.0 );
	gl_FragColor = vec4( (vec2(0.5, 0.5) + normalize(dir)*0.5), gradpower, 1.0 );


/*	
	highp vec3 g  = vec3(vec3(dot(u.xyz, u.xyz), 
                             dot(v.xyz, v.xyz), 
                             dot(u.xyz, v.xyz)) );

	highp float lambda1 = 0.5 * (g.y + g.x + 
              sqrt(g.y*g.y - 2.0*g.x*g.y + g.x*g.x + 4.0*g.z*g.z));
    highp vec2 vv = vec2(g.x - lambda1, g.z);

    highp vec4 dir = (length(vv) > 0.0)? 
        vec4(normalize(vv), sqrt(lambda1), 1.0) : 
        vec4(0.0, 1.0, 0.0, 1.0);

	gl_FragColor = dir;
	*/
/*
    highp vec2 dabs = abs(dir);
	    float ds = 1.0 / ((dabs.x > dabs.y)? dabs.x : dabs.y);
    	dir /= img_size;	
    	*/
}
