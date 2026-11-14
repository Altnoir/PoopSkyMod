#version 150
uniform float GameTime;
uniform vec4 ColorModulator;
in vec4 vertexColor;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    float time = GameTime * 1200.0;
    float u = texCoord.x;
    float v = (texCoord.y - 0.5) * 2.0;
    float wave = sin(u * 30.0 - time * 2.5) * 0.15;
    float core = exp(-pow((v - wave) * 9.0, 2.0));
    float halo = exp(-v * v * 5.0) * 0.12;
    float flow = pow(0.5 + 0.5 * cos(u * 45.0 - time * 5.0), 12.0);
    vec2 cell = vec2(u * 32.0 - time * 1.8, texCoord.y * 5.0);
    vec2 star = fract(cell) - 0.5;
    float seed = fract(sin(dot(floor(cell), vec2(127.1, 311.7))) * 43758.5453);
    float sparkle = pow(max(0.0, 1.0 - abs(star.x) * 7.0), 8.0)
                  * pow(max(0.0, 1.0 - abs(star.y) * 7.0), 8.0) * step(0.65, seed);
    float alpha = (core * (0.55 + flow * 0.45) + halo + sparkle)
                * (1.0 - smoothstep(0.6, 1.0, abs(v)));
    vec3 tint = vertexColor.rgb;
    vec3 color = tint * (0.9 + 0.1 * flow);
    fragColor = vec4(color, alpha * vertexColor.a) * ColorModulator;
}
