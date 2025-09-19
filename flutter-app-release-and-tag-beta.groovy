pipeline {
    agent { label 'flutter' }

    stages {
        stage('Checkout') {
            steps {
            	git credentialsId: 'tesla-android-jenkins', url: 'git@github.com:tesla-android/flutter-app.git', branch: "develop"
            }
        }
        
        stage('Set version') {
           steps {
                script {
                	RELEASE_VERSION = getVersion();
        		}
            }
        }
        
        stage('Get dependencies') {
            steps {
                sh('fvm flutter pub get -v')
            }
        }
        stage('Build runner') {
            steps {
                sh('fvm flutter packages pub run build_runner build --delete-conflicting-outputs')
            }
        }
        stage('Build') {
            steps {
                script {
                    SENTRY_RELEASE = 'flutter-app-release-' + RELEASE_VERSION
                }
                sh('fvm flutter build web --no-web-resources-cdn')
            }
        }
        stage('Prepare artifacts') {
            steps {
                sh('cd build/web && zip tesla-android-' + SENTRY_RELEASE + '.zip -r *')
            }
        }
        stage('Tag release') {
        	steps {
        		script {
        			BETA_VERSION = RELEASE_VERSION + '-beta'
                }
        		sh 'gh release create ' + BETA_VERSION + ' --generate-notes ./build/web/*.zip'
        	}
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'build/web/*.zip', fingerprint: true
            cleanWs()
        }
        failure {
            cleanWs()
        }
    }
}

@NonCPS
def getVersion() {
	File pubspec = new File(WORKSPACE + '/pubspec.yaml')
	def version = pubspec.text =~ /(([0-9]+)\.([0-9]+)\.([0-9]+)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\[0-9A-Za-z-])?)/
	return version[0][0]
}
