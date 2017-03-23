uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
varying mediump vec2 backbufferCoord;
uniform mediump vec2 backbufferSize;
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying mediump vec2 tex1Coord;
uniform mediump float loopsize;

void main (void)
{
	mediump vec2 pmul = vec2( loopsize, loopsize/backbufferSize.x*backbufferSize.y );
	mediump vec2 pos = backbufferCoord * pmul;
	
	// 	skew
	mediump float F2 = 0.3660254037844386; // 0.5*(sqrt(3.0)-1.0);
	mediump float s = (pos.x + pos.y)*F2;
	mediump vec2 ij=vec2(floor( pos.x + s ), floor(pos.y + s ) );
	
	
	mediump float G2 = 0.2113248654051871; //(3.0 - sqrt(3.0))/6.0;
	mediump float t = (ij.x+ij.y)*G2;
	// UNSKEWED BACK!
	mediump vec2 pos0 = vec2(ij.x-t,ij.y-t);
	
	mediump vec2 xy0 = pos - pos0;
	mediump vec2 ij1;
	if (xy0.x>xy0.y) ij1 = vec2(1,0); else ij1 = vec2(0,1);
		
	mediump vec2 xy1 = xy0 - ij1 + vec2(G2,G2);
	mediump vec2 xy2 = xy0 - vec2(1,1) + 2.0 * vec2(G2, G2);
	
	// try direct blending?
	mediump float t0 = dot(xy0, xy0);
	t0 = clamp( 0.5 - t0, 0.0, 1.0 );
	
	mediump float t1 = dot(xy1, xy1);
	t1 = clamp( 0.5 - t1, 0.0, 1.0 );
	
	mediump float t2 = dot(xy2, xy2);
	t2 = clamp( 0.5 - t2, 0.0, 1.0 );

	
	mediump vec2 pos1=(pos-xy1);
	mediump vec2 pos2=(pos-xy2);
		
	mediump float tot = t0+t1+t2;
	mediump vec3 tcol = texture2D( backbuffer, pos0/pmul ).rgb * t0 / tot +
						texture2D( backbuffer, pos1/pmul ).rgb * t1 / tot +
						texture2D( backbuffer, pos2/pmul ).rgb * t2 / tot;
	
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
	gl_FragColor = vec4(tcol,vertexColor.a*alphamul);
}
