pipeline {
    agent { label 'flutter' }

    stages {
        stage('Checkout') {
            steps {
            	git credentialsId: 'tesla-android-jenkins', url: 'git@github.com:tesla-android/flutter-app.git', branch: "main"
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
                sh('flutter pub get -v')
            }
        }
        stage('Build runner') {
            steps {
                sh('flutter packages pub run build_runner build --delete-conflicting-outputs')
            }
        }
        stage('Build') {
            steps {
                script {
                    SENTRY_RELEASE = 'flutter-app-release-' + RELEASE_VERSION
                }
                sh('flutter build web --source-maps --dart-define=SENTRY_RELEASE=' + SENTRY_RELEASE)
            }
        }
        stage('Upload debug symbols to Sentry') {
            steps {
                sh('sentry-cli releases new ' + SENTRY_RELEASE)
                sh('sentry-cli releases files ' + SENTRY_RELEASE + ' upload-sourcemaps build/web --ext js')
                sh('sentry-cli releases finalize ' + SENTRY_RELEASE)
            }
        }
        stage('Prepare artifacts') {
            steps {
                sh('cd build/web && zip tesla-android-' + SENTRY_RELEASE + '.zip -r *')
            }
        }
        stage('Tag release') {
        	steps {
        		sh 'gh release create ' + RELEASE_VERSION + '--generate-notes ./build/web/*.zip'
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
