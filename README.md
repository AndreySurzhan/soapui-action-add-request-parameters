# Description
That Plugin/Action should allow you to populate specified parameters in selected requests across selected soapUI project

![Alt text](/src/main/resources/images/screenShot.png?raw=true "Add Request Parameters")

# Installation

1. Install [maven](https://maven.apache.org/install.html)
2. run `mvn package` to build the `jar` file
3. Copy `./target/soapui-action-add-request-parameters-<VERSION>.jar` file into `soapui/bin/ext` folder
4. Create xml file `add-request-parameters-actions.xml` with the content

    ```
    <?xml version="1.0" encoding="UTF-8"?>
    <tns:soapui-actions xmlns:tns="http://eviware.com/soapui/config">

    <!-- defined action -->
    <tns:action id="AddRequestParameters" actionClass="com.soapui_action_add_request_parameters.AddRequestParameters"/>

    <!-- add action to project popup -->
    <tns:actionGroup id="EnabledWsdlProjectActions">
    <tns:actionMapping actionId="AddRequestParameters"/>
    </tns:actionGroup>

    </tns:soapui-actions>

    ```

5. Put `add-request-parameters-actions.xml` file into `soapui/bin/actions`
6. Restart SoapUI
7. Select any project -> click on "Project" dropdown -> you should be able to see "Add Request Parameters"
8. Use it well
