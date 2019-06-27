run:
	docker run \
  --rm \
  -p 8080:8080 \
  -p 50000:50000 \
	-v ${PWD}/jenkins_home:/var/jenkins_home \
  jenkinsci/blueocean

commit:
	git add .
	git commit -m "update at `date '+%Y-%m-%d %H:%M:%S'`"
	git push
