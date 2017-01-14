#version 330

#include <view_block.glsl>

uniform vec4 materialColor;

in vec4 vertexPosition;
in vec4 vertexColor;
in vec2 vertexTexCoord;

out vec4 vsColor;
out vec2 vsTexCoord;

void main() {
	vsColor = materialColor;

    vsTexCoord = vertexTexCoord;

    mat4 viewMatrixWithoutTranslation = mat4(
        view.viewMatrix[0][0], view.viewMatrix[0][1], view.viewMatrix[0][2], view.viewMatrix[0][3],
        view.viewMatrix[1][0], view.viewMatrix[1][1], view.viewMatrix[1][2], view.viewMatrix[1][3],
        view.viewMatrix[2][0], view.viewMatrix[2][1], view.viewMatrix[2][2], view.viewMatrix[2][3],
        0, 0, 0, view.viewMatrix[3][3]
    );

	gl_Position =  view.projMatrix * viewMatrixWithoutTranslation * vertexPosition;
}
