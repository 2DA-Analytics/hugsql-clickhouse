pipeline {
  agent {
    label 'boot'
  }
  stages {
    stage("install") {
      steps {
        sh "DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata"
        sh "echo 'US/Chicago' | tee /etc/timezone"
        sh "dpkg-reconfigure --frontend noninteractive tzdata"
        sh "apt-get install -y dirmngr"
        sh "apt-key adv --keyserver keyserver.ubuntu.com --recv E0C56BD4"
        sh "echo 'deb http://repo.yandex.ru/clickhouse/deb/stable/ main/' | tee /etc/apt/sources.list.d/clickhouse.list"
        sh "apt-get update"
        sh "DEBIAN_FRONTEND=noninteractive apt-get install -y clickhouse-server"
        sh "service clickhouse-server start"
      }
    }
    stage("test") {
      steps {
        sh "boot bat-test -c"
        publishHTML (target: [
          reportDir: 'target/coverage',
          reportFiles: 'index.html',
          reportName: 'HugSQL Clickhouse Cloverage'
        ])
      }
    }
    stage("static analysis") {
      steps {
        sh "boot with-bikeshed"
        sh "boot with-eastwood"
        sh "boot with-kibit"
      }
    }
  }
  post {
    failure {
      slackSend color: "warning", message: "${env.JOB_NAME} failed to build: (<${env.BUILD_URL}|Open>)."
    }
    success {
      slackSend color: "good", message: "${env.JOB_NAME} successfully built: (<${env.BUILD_URL}|Open>)."
    }
  }
}