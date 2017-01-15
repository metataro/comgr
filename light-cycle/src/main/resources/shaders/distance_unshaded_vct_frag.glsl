#version 330

uniform sampler2D colorMap;

in vec4 vsColor;
in vec2 vsTexCoord;
in float alphaMod;

out vec4 fragColor;

void main() {
    // Calculate color normally but use distance related alpha
    vec4 rawColor = vsColor * texture(colorMap, vsTexCoord);
	fragColor = vec4(rawColor.rgb, rawColor.a * alphaMod);
}
