#!groovy

def call(String appUrl = null) {
    if (!appUrl) {
        appUrl = env.SLACK_BOT_URL as String
    }

    try {
        String bitbucketProject = getBitbucketProject(env.GIT_URL as String)
        String bitbucketRepo = getBitbucketRepo(env.GIT_URL as String)
        String jobId = getJobId(env.JOB_NAME as String)
        def branchName = env.BRANCH_NAME ?: 'none'
        def success = currentBuild.result in ['SUCCESS', null]

        def body = """
        {
            "bitbucketProject": "$bitbucketProject",
            "bitbucketRepo": "$bitbucketRepo",
            "jobId": "$jobId",
            "branchName": "$branchName",
            "buildUrl": "$env.BUILD_URL",
            "success": $success
        }
        """

        httpRequest url: appUrl, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: body
    } catch (e) {
        return
    }
}


// Due to https://issues.jenkins-ci.org/browse/JENKINS-44278
private static String getJobId(String jobName) {
    def jobNameParts = jobName.tokenize('/')
    jobNameParts.size() < 2 ? jobName : jobNameParts[-2]
}

private static String getBitbucketProject(String gitUrl) {
    gitUrl.tokenize('/')[-2]
}

private static String getBitbucketRepo(String gitUrl) {
    def matcher = gitUrl =~ '([a-z-]+)\\.git'
    matcher[0].last()
}
