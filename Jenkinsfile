node {
    stage 'Checkout'
    checkout scm

    def mvnEnv = ["PATH+MAVEN=${tool 'mvn'}/bin"]

    stage 'Compile'
    withEnv(mvnEnv) {
        sh 'mvn -B compile'
    }

    stage 'Unit testing'
    withEnv(mvnEnv) {
        sh 'mvn -B test'
    }

    stage 'Deploy'
    withEnv(mvnEnv) {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'maven.deploy', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME']]) {
            sh 'mvn deploy -Prelease -Dmaven.settings.username=${USERNAME} -Dmaven.settings.password=${PASSWORD}'
        }
    }

}
