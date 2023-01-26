import React from "react";
import axios from "axios";
import { deleteContract } from "../util/utilities";

class Main extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            contracts: []
        };

        this.createNew = this.createNew.bind(this);
        this.closeApp = this.closeApp.bind(this);
    }

    componentDidMount() {
      axios.get("http://localhost:8080/all")
          .then(response => {
              console.log(response);
              this.setState({
                  contracts: response.data.body
              });
              this.listContracts(response.data.body);
          });
    }

    listContracts(contracts) {
        let overviewTable = document.getElementById("overviewTableBody");

        for (const element of contracts) {
            let contract = element;
            console.log(contract);
            let row = document.createElement("tr");

            let data = [contract.id, contract.name, contract.category, contract.expenses,
                contract.cycle, contract.description];

            for (let k = 0; k <= 5; k++) {
                let tdTag = document.createElement("td");
                tdTag.innerHTML = data[k];
                switch (k) {
                    case 3:         // expenses -> euros
                        tdTag.innerHTML += " &euro;";
                        break;
                    case 4:         // pament cycle -> months
                        tdTag.innerHTML += " months";
                        break;
                    case 5:         // description -> multiline
                        tdTag.innerHTML = data[k].replace("\\n", "<br />");
                        break;
                    default:
                }
                row.append(tdTag);
            }
            let actionTdTag = document.createElement("td");
            let detailsButton = document.createElement("button");
            detailsButton.type = "button";
            detailsButton.innerText = "Details";
            detailsButton.onclick = function () {
                window.location.href = "/contract/" + element.id;
            };
            let deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.innerText = "Delete";
            deleteButton.onclick = () => { deleteContract(data[0], data[1]); };

            actionTdTag.append(detailsButton);
            actionTdTag.append(deleteButton);
            row.append(actionTdTag);

            overviewTable.append(row);
        }
    }

    createNew() {
        window.location.href = "/new";
    }

    closeApp() {
        axios.get("http://localhost:8080/prepareShutdown")
            .then(async (result) => {
                const root = await navigator.storage.getDirectory();
                const draftHandle = await root.getFileHandle('test.txt', {create: true})
            })

        /*axios.get("http://localhost:8080/shutdown")
            .then(result => {
                console.log(result);
                axios.post("http://localhost:8080/actuator/shutdown")
                    .then(finalResult => {
                        if (finalResult.status === 200)
                            window.close();
                    })
            })
            .catch(e => {
                console.error(e);
                alert("File couldn't be saved");
            })*/
    }

    render() {
        return (
            <>
                <div className="custom-flex">
                    <h1>Overview</h1>
                    <div>
                        <button type="button" onClick={this.createNew}>Create new</button>
                        <button type="button" onClick={this.closeApp}>Shutdown</button>
                    </div>
                </div>
                <table id="overviewTable">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Expenses</th>
                        <th>Payment cycle</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody id="overviewTableBody">

                    </tbody>
                </table>
            </>
        )
    }
}

export default Main;