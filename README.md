# Utility library for Axway API-Management

This library provides useful functionality for the Axway API management solution. They can be easily integrated into your policies via scripting filters.

## Functions: 

- Get [List of subscribers](#get-api-subscribers) for an API

## Installation

To install, download the [release package](https://github.com/Axway-API-Management-Plus/apim-utils/releases) and install it under `ext/lib`. After that, restart the API Gateway. It is recommended to make the jars files known in the Policy Studio as well as it is describe here: https://docs.axway.com/bundle/axway-open-docs/page/docs/apim_policydev/apigw_poldev/general_ps_settings/index.html#runtime-dependencies

### Get API Subscribers

For example, if you want to send email notifications to all developers, then you can use this function to create a list of subscribers. 
This is based on the applications that have an active subscription to the API. Users who have created the application and are allowed to manage it will be taken into account. 
Furthermore, you can set a custom property for users, which controls whether they should receive a notification or not.

Here is an example for Groovy:  

```groovy
import com.axway.apim.utils.APIManagerUtils;
import com.vordel.trace.Trace

def invoke(msg)
{
    // Get the ID of the API (perhaps from an Alert)
    def apiId = msg.get("api.id");
    // Get/Create an instance of the API-Manager utils
    def apimUtils = APIManagerUtils.getInstance("apiadmin", "changeme");
    try {
        // Use the APIM-Utils to get the subscribers list
        def subscribers = APIManagerUtils.getInstance("apiadmin", "changeme").getSubscribers(apiId);
        // You can also provide the name of a custom property a user can set to disable notifications
        // def subscribers = APIManagerUtils.getInstance("apiadmin", "changeme").getSubscribers(apiId, "notify");
        msg.put("apiSubscribers", subscribers);
        return true;
    } catch (Exception e) {
        Trace.error('Error getting API-Subscribers.', e);
        return false;
    }
}
```

#### Notification toggle

You can give users the option to disable notifications. To do this, you configure a corresponding custom property for users in the API Manager, which is then used by the function. Here is an example:

```
        user: {
            notify: {
                label: 'Notification',
                required: false,
                help: "If true, user will get notifications from the platform.",
                type: "switch",
                options: [
                    {value: true, label: 'True'},
                    {value: false, label: 'False'}
                ]
            }
        }
```

## API Management Version Compatibilty

This artefact has been tested with API-Management Versions

| Version            | Comment         |
| :---               | :---            |
| 7.7-20210830       |                 |
| 7.7-20210530       |                 |
| 7.7-20210330       |                 |
| 7.7-20200930       |                 |

Please let us know, if you encounter any [issues](https://github.com/Axway-API-Management-Plus/utils/issues) with your API-Manager version.  

## Contributing

Please read [Contributing.md](https://github.com/Axway-API-Management-Plus/Common/blob/master/Contributing.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Team

![alt text][Axwaylogo] Axway Team

[Axwaylogo]: https://github.com/Axway-API-Management/Common/blob/master/img/AxwayLogoSmall.png  "Axway logo"


## License
[Apache License 2.0](/LICENSE)
