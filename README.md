# Model Railroad Scaler

This project is designed to calculate scaled dimensions for model railroad planning. It will convert
from full size (i.e., prototype) to model dimensions and vice versa. This project recognizes the 
following scales:

* O (1:48)
* S (1:64)
* OO (1:76)
* HO (1:87.1)
* TT (1:120)
* N (1:160)
* Z (1:220)

# Usage

You can use this project in three ways:

1. By running the application as an HTTP server that displays a web page allowing you to input the data fields and get scaled output.
  
1. By running a manual test that allows you to set the fields in the test and get the scaled results when the test is run.

1. By running the application as an HTTP server and sending an HTTP POST request to the application with the input as JSON in the HTTP request body.

## HTTP Server Mode

Build the project as a JAR file. Using Maven: **mvn install**.

Run the JAR file: **java -jar scaler-{{version}}.jar**

Change the port from 8080: **java -Dserver.port=8500 -jar scaler-{{version}}.jar**

View the web page in a browser: **http://localhost:8080/scale** (substitute the correct hostname and port).

## Manual Test Mode

Open the test file com.goosebumpdesigns.scaler.ManualScale.java in src/test/java. Change the variables in the test to whatever you want. Run the test. The scaled output is printed to the console.

## HTTP Server Mode using JSON

You can opt out of using the web page by running the application and sending JSON to it using a REST client or some other client. If you opt to do this, the JSON must be in one of the following formats.

In the requests below, the following scales are valid: O, S, OO, HO, TT, N, and Z.

These are the valid measurement types: CM, MM, INCH, FOOT.
 

### Calculate model dimensions from full size dimensions

#### Sample Request

```
{
  "scale" : "HO",
  "outputMeasurement" : "CM",
  "fullsizeDimensions" : {
    "length" : {
      "value" : 40.00,
      "measurement" : "FOOT"
    },
    "width" : {
      "value" : 12.50,
      "measurement" : "FOOT"
    },
    "height" : {
      "value" : 147.00,
      "measurement" : "INCH"
    }
  }
}
```

#### Sample Response
```
{
  "scale":"HO",
  "outputMeasurement":"CM",
  "modelDimensions":{
    "length":{
      "value":14.00,
      "measurement":"CM"
    },
    "width":{
      "value":4.37,
      "measurement":"CM"
    },
    "height":{
      "value":4.29,
      "measurement":"CM"
    }
  },
  "fullsizeDimensions":{
    "length":{
      "value":40.00,
      "measurement":"FOOT"
    },
    "width":{
      "value":12.50,
      "measurement":"FOOT"
    },
    "height":{
      "value":147.00,
      "measurement":"INCH"
    }
  }
}
```

### Calculate full size dimensions from model dimensions

#### Sample Request

```
{
  "scale" : "HO",
  "outputMeasurement" : "FOOT",
  "modelDimensions" : {
    "length" : {
      "value" : 18.75,
      "measurement" : "CM"
    },
    "width" : {
      "value" : 4.23,
      "measurement" : "CM"
    },
    "height" : {
      "value" : 27.50,
      "measurement" : "MM"
    }
  }
}
```

#### Sample Response

```
{

  "scale":"HO",
  "outputMeasurement":"FOOT",
  "modelDimensions":{
    "length":{
      "value":18.75,
      "measurement":"CM"
    },
    "width":{
      "value":4.23,
      "measurement":"CM"
    },
    "height":{
      "value":27.50,
      "measurement":"MM"
    }
  },
  "fullsizeDimensions":{
    "length":{
      "value":53.58,
      "measurement":"FOOT"
    },
    "width":{
      "value":12.09,
      "measurement":"FOOT"
    },
    "height":{
      "value":7.86,
      "measurement":"FOOT"
    }
  }
}
```