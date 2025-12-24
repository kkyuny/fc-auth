## K8S 환경 구축하기
1. minikube 소개

minikube 는 가벼운 K8S 구현체 입니다. 로컬 머신에 클러스터를 구성하기 위해 vm을 사용합니다.
vm을 돌릴 도구가 필요합니다 (docker 추천)
2. vm 도구 설치

brew install docker
3. kubectl & minikube 설치

brew install kubectl

brew install minikube

4. 설치 완료 테스트

docker desktop 실행   https://www.docker.com/products/docker-desktop/

minikube start 혹은 minikube start --vm-driver=docker
5. kubectl get all