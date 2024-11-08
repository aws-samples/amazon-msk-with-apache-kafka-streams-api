## Running Apache Kafka Streams at scale on AWS Fargate with Amazon MSK
Build a real-time stream processing application using [Amazon MSK](https://aws.amazon.com/msk/), [AWS Fargate](https://aws.amazon.com/fargate/) and the [Apache Kafka Streams API](https://kafka.apache.org/documentation/streams/). Kafka Streams API is a client library that simplifies development of stream applications. Behind the scenes Kafka Streams library is really an abstraction over standard Kafka Producer and Kafka Consumer API. When you build applications with Kafka Streams library your data streams are automatically made fault tolerant, and are transparently, and elastically distributed over the instances of the applications. Kafka Streams applications are supported by Amazon MSK. AWS Fargate is a serverless compute engine for containers that works with AWS container orchestration services like [Amazon Elastic Container Service (Amazon ECS)](https://aws.amazon.com/ecs/), which allows us to easily run, scale, and secure containerized applications.

## Architecture
Our streaming application architecture will consist of Stream Producer, which will connect to Twitter Stream API, read tweets and publish to MSK. Kafka Streams Processor will consume these messages, perform window aggregation, push to topic result, and also print out to logs. Both apps will be hosted on AWS Fargate as service.

![Architecture](misc/MSK_Kafka_Streams_Fargate.png)

You can find a further details and a more thorough description and discussion of the architecture on the [AWS Big Data Blog](https://aws.amazon.com/blogs/big-data/power-your-kafka-streams-application-with-amazon-msk-and-aws-fargate/).

## Prerequisites

Make sure to complete the following steps as prerequisites:

1. Create an AWS account. For this post, you configure the required AWS resources in the `us-east-1`, `us-west-2` or `eu-central-1` Region. If you haven’t signed up, complete the following tasks:

    a. Create an account. For instructions, see [Sign Up for AWS](https://aws.amazon.com/resources/create-account/).
    
    b. Create an [AWS Identity and Access Management](https://aws.amazon.com/iam/) (IAM) user. For instructions, see [Create an IAM User](https://lakeformation.aworkshop.io/30-prerequisite/302-create-iam-account.html).
    
2. Install [Docker](https://docs.docker.com/get-docker/) on your local machine.
3. Install Java 21 on your local machine.

## Setup

1. Clone this repository:
   ```
    git clone https://github.com/aws-samples/amazon-msk-with-apache-kafka-streams-api
   ```
2. Deploy the cloud formation template `./misc/cfn-kafka-streams-msk.yml` in your account
3. Build the applications by running the following command in the root of the project:
   ```
   ./gradlew clean build
   ```

4. Create your Docker images (kafka-streams-msk and twitter-stream-producer):
   ```
   docker-compose build
   ```

5. Retrieve an authentication token and authenticate your Docker client to your registry. Use the following AWS Command Line Interface (AWS CLI) code:
   ```
   aws ecr get-login-password --region <<region>> | docker login --username AWS --password-stdin <<account_id>>.dkr.ecr.<<region>>.amazonaws.com
   ```

6. Tag and push your images to the Amazon ECR repository:
   ```
   docker tag kafka-streams-msk:latest  <<account_id>>.dkr.ecr.<<region>>.amazonaws.com/kafka-streams-msk:latest 
   docker tag twitter-stream-producer:latest  <<account_id>>.dkr.ecr.<<region>>.amazonaws.com/twitter-stream-producer:latest
   ```

7. Run the following command to push images to your Amazon ECR repositories:
   ```
   docker push <<account_id>>.dkr.ecr.<<region>>.amazonaws.com/kafka-streams-msk:latest 
   docker push <<account_id>>.dkr.ecr.<<region>>.amazonaws.com/twitter-stream-producer:latest
   ```

8. Ensure that the cloud formation template `cfn-kafka-streams-msk.yml` is deployed
9. Deploy the cloud formation template `./misc/cfn-ecs-fargate.yml` in your account




## Project Structure

- `/kafka-streams-msk/*`: Kafka Streams implementation including Dockerfile
- `/misc/`: Contains the cloud formation definition for the ECS and MSK cluster
- `/twitter-stream-producer/`: Twitter Stream producer

## Cleaning Up

To remove all resources created by this project:

1.	Delete the ECS CloudFormation stack. You can delete these resources via the AWS CloudFormation console or via the AWS Command Line Interface (AWS CLI).
2.	Navigate to Amazon Elastic Container Registry. Then in Private registry click on Repositories. Delete the kafka-streams-msk and  twitter-stream-producer repositories
3.	Delete the MSK CloudFormation stack.


## List of resources created

1. Amazon EC2 (Elastic Compute Cloud):
   - EC2 Instance (KafkaClientEC2Instance)
   - Security Groups (KafkaClientInstanceSecurityGroup, MSKSecurityGroup)

2. Amazon VPC (Virtual Private Cloud):
   - VPC
   - Subnets (1 Public: PublicSubnetOne, 3 Private: PrivateSubnetOne, PrivateSubnetTwo, PrivateSubnetThree)
   - Internet Gateway
   - NAT Gateway
   - Route Tables (PublicRouteTable, PrivateRouteTable)
   - Routes (PublicRoute, DefaultPrivateRoute)
   - VPC Gateway Attachment

3. Amazon ECS (Elastic Container Service):
   - ECS Cluster (Cluster)
   - Task Definitions (TaskDefinition1, TaskDefinition2)
   - ECS Services (Service1, Service2)

4. AWS Fargate:
   - Used as the launch type for ECS services

5. Amazon ECR (Elastic Container Registry):
   - ECR Repositories (EcrRepository1, EcrRepository2)

6. Amazon MSK (Managed Streaming for Apache Kafka):
   - MSK Cluster (MSKCluster)

7. AWS IAM (Identity and Access Management):
   - IAM Roles (ExecutionRole, TaskRole, EC2Role)
   - IAM Policies (MSKIAMPolicy)
   - Instance Profile (EC2InstanceProfile)

8. Amazon CloudWatch Logs:
   - Log Groups (LogGroup1, LogGroup2)

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the MIT-0 License. See the [LICENSE](LICENSE) file for details.