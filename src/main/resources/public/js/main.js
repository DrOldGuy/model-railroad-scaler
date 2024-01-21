/**
 * This file defines the Main "class". This class is responsible for managing input and output on
 * the Model Railroad Scaler main page.
 */
var Main = {
  POST_URL: "/scale",
  _lengthEntered: false,

  /**
   * Initialize the page event handlers.
   */
  init: function() {
    const $form = $("#scaler")

    Main._initEventHandlers($form);
  },

  /**
   * Initialize the event handlers.
   */
  _initEventHandlers: function($form) {
    $form.find("select").on("change", () => {
      Main._upatePageFields.call($form);
    });

    $form.find("input").on("keyup", () => {
      Main._lengthEntered = true;
      Main._upatePageFields.call($form);
    });
  },

  /**
   * Update the page fields on a field change.
   */
  _upatePageFields: function() {
    const $form = this;

    $("#error-message").hide();
    
    Main._updateOutputText.call($form);

    if (!Main._lengthEntered) {
      return;
    }

    if (Main._isPageError.call($form)) {
      return;
    }

    const data = Main._gatherFormData.call($form);
    const json = JSON.stringify(data);

    /*
     * Make the AJAX call to fill in the missing fields in the data object. For example, if
     * full size dimensions are supplied by the user, model dimensions are returned, and vice versa.
     */
    $.ajax(Main.POST_URL, {
      contentType: "application/json",
      data: json,
      dataType: "json",
      method: "POST"
    })
    .done((data) => {
        Main._displayData.call($form, data);
      })
    .fail((xhr, _textStatus, _errorThrown) => {
        const err = JSON.parse(xhr.responseText);
        $("#error-message").text("Status code: " + err.errorCode + " (" + err.message + ")").show();
      });
  },

  /**
   * Update the output type ("model"|"fullsize") and the output measurement
   */  
  _updateOutputText: function() {
    const $form = this;
    const type = $form.find("select[name=type]").val();
    const $outputType = $("#output-type");
    const output = $form.find("select[name=outputMeasurement]").val();
    const $outputMeasurement = $("#outputMeasurement");

    $outputType.text(type == "model" ? "Full size" : "Model");

    switch (output) {
      case "CM": $outputMeasurement.text("centimeters."); break;
      case "FOOT": $outputMeasurement.text("feet."); break;
      case "INCH": $outputMeasurement.text("inches."); break;
      case "MM": $outputMeasurement.text("millimeters."); break;
    }

  },

  /**
   * Display the data returned by the server.
   */
  _displayData: function(data) {
    const $form = this;
    const type = $form.find("select[name=type]").val();
    const dimensions = type == "model" ? data.fullsizeDimensions : data.modelDimensions;
    
    if (dimensions.length) {
      $("#output-length").val(dimensions.length.value.toFixed(2));
    }

    if (dimensions.width) {
      $("#output-width").val(dimensions.width.value.toFixed(2));
    }

    if (dimensions.height) {
      $("#output-height").val(dimensions.height.value.toFixed(2));
    }
  },

  /**
   * Check to see if there is an error on the page. If so, set the error status.
   */
  _isPageError: function() {
    const $form = this;

    const $length = $form.find("input[name=length]");
    const $width = $form.find("input[name=width]");
    const $height = $form.find("input[name=height]");
    const hasError = !($length.val() || $width.val() || $height.val());

    Main._setError.call($form, $length, hasError);
    Main._setError.call($form, $width, hasError);
    Main._setError.call($form, $height, hasError);
  },

  /**
   * Set the error condition.
   */
  _setError: function($input, hasError) {
    const $message = $input.closest("div.prompt-field").find("div.message");

    if (hasError) {
      $input.addClass("error");
      $message.fadeIn();
    }
    else {
      $input.removeClass("error");
      $message.fadeOut();
    }
  },

  /**
   * Collect the form data and return a JavaScript object.
   */
  _gatherFormData: function() {
    const $form = this;

    const data = {};
    const dimensions = {};

    data.scale = $form.find("select[name=scale]").val();
    data.outputMeasurement = $form.find("select[name=outputMeasurement]").val();

    Main._addMeasurement.call($form, dimensions, "length");
    Main._addMeasurement.call($form, dimensions, "width");
    Main._addMeasurement.call($form, dimensions, "height");

    const type = $form.find("select[name=type]").val();

    if (type == "fullsize") {
      data.fullsizeDimensions = dimensions;
    }
    else {
      data.modelDimensions = dimensions;
    }

    return data;
  },

  _addMeasurement: function(dimensions, name) {
    const $form = this;
    const measurement = Main._getMeasurement.call($form, name);

    if (measurement) {
      dimensions[name] = measurement;
    }
  },

  /**
   * Returns a value and a measurement as an object with value and measurement fields or null if no
   * value.
   */
  _getMeasurement: function(name) {
    const $form = this;
    const $field = $form.find("input[name=" + name + "]");
    const value = $field.val();
    const measurement = $field.closest(".prompt-field").find("select").val();

    if (value) {
      return {
        value: value,
        measurement: measurement
      };
    }

    return null;
  }
}

$(function() {
  Main.init();
});

