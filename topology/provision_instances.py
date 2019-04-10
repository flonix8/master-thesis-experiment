import json
from argparse import ArgumentParser


class Node:
    def __init__(self, name, public_ip, internal_ip):
        self.name = name
        self.public_ip = public_ip
        self.internal_ip = internal_ip

    def __repr__(self):
        return f'Node(name={self.name}, public_ip={self.public_ip}, internal_ip={self.internal_ip})'
    
    @classmethod
    def from_json(cls, json_node):
        name = json_node['name']
        public_ip = json_node['public_ip']
        internal_ip = next(iter(json_node['edgesBack'].values()))['addr']
        return cls(name, public_ip, internal_ip)


def read_topology_file(file_name):
    with open(file_name) as file:
        data = json.load(file)

    doc = next(filter(lambda x: x['id'] == 0, data[0]['allDocs']))
    return list(doc['allNodes'].values())


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('topology_file')
    args = parser.parse_args()
    
    nodes = [Node.from_json(node) for node in read_topology_file(args.topology_file)]
    print(nodes)
