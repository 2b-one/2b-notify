#!groovy

def call(String appUrl = null) {
    if (!appUrl) {
        appUrl = env.SLACK_BOT_URL as String
    }

    try {
        if (params.AUTHOR) {
            if (!currentBuild.description) {
                currentBuild.description = ''
            }

            currentBuild.description += "Author: ${params.AUTHOR}\n"
        }

        String body = ''
        String trackId = params.TRACK_ID ?: 'none'
        def success = currentBuild.result in ['SUCCESS', null]
        if (success) {
            body = """
            {
                "trackId": "$trackId",
                "buildUrl": "$env.BUILD_URL",
                "nomadUrl": "https://hud.atc/nomad/${env.NOMAD_REGION}/jobs/${env.NOMAD_ID}/info",
                "envUrl": "https://${env.NOMAD_ID}.${env.NOMAD_DC}.atc",
                "success": $success
            }
            """
        } else {
            body = """
            {
                "trackId": "$trackId",
                "buildUrl": "$env.BUILD_URL",
                "success": $success
            }
            """
        }

        httpRequest url: appUrl, contentType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: body
    } catch (e) {
        return
    }

}