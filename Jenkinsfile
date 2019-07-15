pipeline {
  agent {
    label 'boot'
  }
  stages {
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
      slackSend color: 'danger', message: 'hugsql-clickhouse failed to build.'
    }
    success {
      slackSend color: 'good', message: 'hugsql-clickhouse successfully built.'
    }
  }
}