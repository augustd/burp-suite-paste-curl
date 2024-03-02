# burp-suite-paste-curl
Burp Suite extension to allow pasting cURL commands into a new tab in Repeater. The pasted cURL command will be parsed 
into a raw HTTP request suitable for use with Repeater.

### Usage
1. Copy a curl request (from browser developer tools, API docs, etc)
2. Right click in a pane where requests are shown (e.g. Proxy, Repeater, etc)
3. Select Extensions -> Paste cURL -> Paste cURL request

A new tab will open in repeater with the parsed raw HTTP request.