
function addDataToTable(tableId, data){
  let tableRef = document.getElementById(tableId);
  const tbody = tableRef.getElementsByTagName('tbody')[0];

  for (let i = 0; i < data.length; i++) {

    let newRow = tbody.insertRow(-1);
    let FilenameCell = newRow.insertCell(0);
    let SpamProbabilityCell = newRow.insertCell(1);
    let ClassCell = newRow.insertCell(2);

    let FilenameText = document.createTextNode(data[i].file);
    let SpamProbabiltyText = document.createTextNode(data[i].spamProbability);
    let ClassText = document.createTextNode(data[i].actualClass);

    FilenameCell.appendChild(FilenameText);
    SpamProbabilityCell.appendChild(SpamProbabiltyText);
    ClassCell.appendChild(ClassText);


  }

}

function addDataToTableAccuracy(tableId, data){
  let tableRef = document.getElementById(tableId);
  const tbody = tableRef.getElementsByTagName('tbody')[0];

  for (let i = 0; i < data.length; i++)
  {

    let newRow = tbody.insertRow(-1);
    let AccuracyCell = newRow.insertCell(0);


    let AccuracyText = document.createTextNode(data);


    AccuracyCell.appendChild(AccuracyText);

  }

}

function addDataToTablePrecision(tableId, data){
  let tableRef = document.getElementById(tableId);
  const tbody = tableRef.getElementsByTagName('tbody')[0];

  for (let i = 0; i < data.length; i++)
  {

    let newRow = tbody.insertRow(-1);
    let PrecisionCell = newRow.insertCell(1);


    let PrecisionText = document.createTextNode(data);


    PrecisionCell.appendChild(PrecisionText);

  }

}



let apiCallURL = "http://localhost:8080/spamDetector-1.0/api/spam";

/**
 * Function makes a HTTP request to an API
 * **/
function requestData(){
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/json", {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
  })
    .then(response => response.json())
    .then(response => addDataToTable("table-content", response));

}

function requestAccuracy(){
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/accuracy", {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
  })
    .then(response => response.json())
    .then(response => addDataToTableAccuracy("table-data", response));

}

function requestPrecision(){
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/precision", {
    method: 'GET',
    headers: {
      'Accept': 'application/json',
    },
  })
    .then(response => response.json())
    .then(response => addDataToTablePrecision("table-data", response));

}

(function () {
  //debugger
  requestData();
  requestAccuracy();
  requestPrecision();
})();


