#extension GL_EXT_shader_texture_lod : enable

#ifndef GL_EXT_shader_texture_lod
lowp vec4 texture2DLodEXT(mediump sampler2D t, mediump vec2 c, lowp float level )
{
	return texture2D(t, c, 3.0+level);
}
#endif


uniform sampler2D sampler2d;
uniform sampler2D backbuffer;
uniform mediump vec2 backbufferSize;
uniform mediump vec3 useColors[4];
varying mediump vec4 vertexColor;
varying mediump vec2 texCoord;
varying mediump vec2 tex1Coord;
uniform highp float level;



mediump float mixmul( mediump vec3 curcol, mediump vec3 newcol, mediump vec3 targetcol ) {
	mediump float div = ( (newcol.r-curcol.r) + (newcol.g-curcol.g) + (newcol.b-curcol.b) );
	mediump vec3 m = (targetcol-curcol) / div;
	return clamp( (m.x+m.y+m.z), 0.0, 1.0);
}


void main (void)
{
	mediump vec3 bg = texture2DLodEXT(backbuffer, tex1Coord,level).rgb;
	
	
	mediump vec4 comp;
	comp.b = mixmul( useColors[0], useColors[1], bg );
	comp.b = clamp( (comp.b - 0.5) * 2.0 + 0.5, 0.0, 1.0);
	mediump vec3 tecol = mix( useColors[0], useColors[1], comp.b );

	comp.g = mixmul(tecol, useColors[2], bg );
	comp.g = clamp( (comp.g - 0.5) * 2.0 + 0.5, 0.0, 1.0);
	tecol = mix( tecol, useColors[2], comp.g );

	comp.r = mixmul(tecol, useColors[3], bg );
	comp.r = clamp( (comp.r - 0.5) * 2.0 + 0.5, 0.0, 1.0);
	tecol = mix( tecol, useColors[3], comp.r );	
	
	/*
		// contrast
	comp = clamp( (comp-vec4(0.5, 0.5, 0.5, 0.5)) * 3.0 + vec4(0.5, 0.5, 0.5, 0.5), 0.0, 1.0);
	tecol = mix( useColors[0], useColors[1], comp.b );
	tecol = mix( tecol, useColors[2], comp.g );
	tecol = mix( tecol, useColors[3], comp.r );	
	*/
	
	
		
	mediump vec2 tv = (texCoord-vec2(0.5, 0.5))*2.0;
	mediump float alphamul = clamp( 1.0-sqrt( tv.x*tv.x + tv.y*tv.y ), 0.0, 1.0);
	gl_FragColor = vec4( tecol, alphamul );
}