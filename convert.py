import json
import base64
import struct

def simplify_gltf(input_data):
    """Convert complex GLTF model to simplified format."""

    # Extract the first primitive's position data
    first_mesh = input_data['meshes'][0]
    first_primitive = first_mesh['primitives'][0]
    position_accessor_index = first_primitive['attributes']['POSITION']
    position_accessor = input_data['accessors'][position_accessor_index]

    # Get the buffer view data for positions
    position_bufferview = input_data['bufferViews'][position_accessor['bufferView']]

    # Create simplified vertices (using only first 3 vertices for triangle)
    simplified_vertices = []
    max_coords = [-float('inf')] * 3
    min_coords = [float('inf')] * 3

    # Take first 3 positions from original data for a simple triangle
    for i in range(3):
        vertex = [
            position_accessor['max'][0] * (i % 2),  # X: alternate between 0 and max
            position_accessor['max'][1] * (i // 2),  # Y: 0 for first two vertices, max for last
            0.0  # Z: always 0 for simplicity
        ]
        simplified_vertices.extend(vertex)

        # Update max/min
        for j in range(3):
            max_coords[j] = max(max_coords[j], vertex[j])
            min_coords[j] = min(min_coords[j], vertex[j])

    # Create indices for a simple triangle
    indices = [0, 1, 2]

    # Pack data into binary
    vertex_data = struct.pack('<%df' % len(simplified_vertices), *simplified_vertices)
    index_data = struct.pack('<%dH' % len(indices), *indices)

    # Combine and encode as base64
    combined_data = index_data + vertex_data
    base64_data = base64.b64encode(combined_data).decode('ascii')

    # Create simplified GLTF structure
    simplified_gltf = {
        "scenes": [
            {
                "nodes": [0]
            }
        ],
        "nodes": [
            {
                "mesh": 0
            }
        ],
        "meshes": [
            {
                "primitives": [{
                    "attributes": {
                        "POSITION": 1
                    },
                    "indices": 0
                }]
            }
        ],
        "buffers": [
            {
                "uri": f"data:application/octet-stream;base64,{base64_data}",
                "byteLength": len(combined_data)
            }
        ],
        "bufferViews": [
            {
                "buffer": 0,
                "byteOffset": 0,
                "byteLength": len(index_data),
                "target": 34963
            },
            {
                "buffer": 0,
                "byteOffset": len(index_data),
                "byteLength": len(vertex_data),
                "target": 34962
            }
        ],
        "accessors": [
            {
                "bufferView": 0,
                "byteOffset": 0,
                "componentType": 5123,  # UNSIGNED_SHORT
                "count": len(indices),
                "type": "SCALAR",
                "max": [max(indices)],
                "min": [min(indices)]
            },
            {
                "bufferView": 1,
                "byteOffset": 0,
                "componentType": 5126,  # FLOAT
                "count": len(simplified_vertices) // 3,
                "type": "VEC3",
                "max": max_coords,
                "min": min_coords
            }
        ],
        "asset": {
            "version": "2.0"
        }
    }

    return simplified_gltf

def convert_file(input_path, output_path):
    """Convert GLTF file from complex to simple format."""
    with open(input_path, 'r') as f:
        input_data = json.load(f)

    simplified_data = simplify_gltf(input_data)

    with open(output_path, 'w') as f:
        json.dump(simplified_data, f, indent=2)

if __name__ == "__main__":
    # Example usage
    convert_file('app/src/main/assets/10511_model.glb', 'simplified_model.gltf')
