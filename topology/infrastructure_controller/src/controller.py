import boto3
import topology as topo

session = boto3.session.Session()

ec2 = session.resource('ec2')

# Create VPC if not existing
existing_vpcs = list(filter(lambda vpc: vpc.tags
                                        and vpc.tags[0]['Key'] == 'Name'
                                        and vpc.tags[0]['Value'] == 'testbed_vpc',
                            ec2.vpcs.all()))

if len(existing_vpcs) > 0:
    testbed_vpc = existing_vpcs[0]
else:
    testbed_vpc = ec2.create_vpc(CidrBlock='10.0.0.0/16')
    testbed_vpc.wait_until_exists()
    testbed_vpc.create_tags(
        Tags=[{
            'Key': 'Name',
            'Value': 'testbed_vpc'
        }]
    )


# Create subnet if not existing

# Create network interfaces if not existing

# Launch nodes

launch_results = []

for node in topo.nodes:
    launch_results += ec2.create_instances(
        ImageId='ami-0ebe657bc328d4e82',
        InstanceType='t3.nano',
        KeyName='mockfog.pem',
        MinCount=1,
        MaxCount=1,
        TagSpecifications=[{
            'ResourceType': 'instance',
            'Tags': [{
                'Key': 'Name',
                'Value': node.name,
            }]
        }],

    )
