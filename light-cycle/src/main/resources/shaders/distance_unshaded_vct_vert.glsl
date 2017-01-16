#version 330

#include <view_block.glsl>

uniform vec4 materialColor;

uniform vec4 objectDistance0;
uniform vec4 objectDistance1;
uniform vec4 objectDistance2;
uniform vec4 objectDistance3;
uniform int objectCount;

// TODO: Use in application
uniform float minDistance = 80;
uniform float maxDistance = 20;

in vec4 vertexPosition;
in vec2 vertexTexCoord;

out vec4 vsColor;
out vec2 vsTexCoord;
out float alphaMod;

void main() {
	vsColor = materialColor;

    vsTexCoord = vertexTexCoord;
	vec4 position = view.viewProjMatrix * vertexPosition;
	gl_Position = position;

    // TODO: This might be really bad performance wise, change!!
	vec4[] objects = vec4[](objectDistance0, objectDistance1, objectDistance2, objectDistance3);

    alphaMod = 0;

	for(int i = 0; i < objectCount; i++)
    {
        // TODO: Converting all object positions for each vertex is really slow, could be improved
        vec4 objectCamPos = view.viewProjMatrix * objects[i];

        // Calculate distance
        float dist = max(0, minDistance - length(position - objectCamPos));

        dist = dist / (minDistance - maxDistance);
        if(dist > 1)
        {
            dist = 1;
        }

        alphaMod += dist;
    }

    if(alphaMod > 1)
    {
        alphaMod = 1;
    }
}
