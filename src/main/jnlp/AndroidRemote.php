<?php
        header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); // Date in past
        header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
        header("Cache-Control: no-store, no-cahce, must-revalidate");
        header("Pragma: no-cache");
        header("Content-Type: application/x-java-jnlp-file");
        header("Content-disposition:attachment; filename=AndroidRemote.jnlp");
        $vncPort=$_GET['vncPort'];
        $mngPort=$_GET['mngPort'];

$script = '<?xml version="1.0" encoding="UTF-8"?>
<jnlp spec="1.0+" codebase="http://mobilelab.uoc.edu/client/" href="AndroidRemote.jnlp">
  <information>
    <title>Android Remote Client</title>
    <vendor>Uoc</vendor>
    <homepage>http://mobilelab.uoc.edu/client/</homepage>
    <description kind="one-line">Android Remote Client</description>
    <desktop/>
  </information>
  <security>
    <all-permissions/>
  </security>
  <resources>
    <j2se version="1.5+" href="http://java.sun.com/products/autodl/j2se"/>
    <jar href="AndroidRemote-2.3.jar" main="true"/>
        <jar href="ddmlib.jar"/>
        <jar href="tightvnc-jviewer.jar"/>
  </resources>
  <application-desc
    name="Android Remote Client"
    main-class="org.uoc.androidremote.client.main.Client">
    <argument>mobilelab.uoc.edu</argument> 
    <argument>'.$vncPort.'</argument> 
    <argument>'.$mngPort.'</argument>
  </application-desc>
  <update check="background"/>
</jnlp>';

echo $script;

?>
