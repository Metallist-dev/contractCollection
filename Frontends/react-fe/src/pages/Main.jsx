import React from "react";
import axios from "axios";

class Main extends React.Component{
    constructor(props) {
        super(props);
        this.state = {
            contracts: []
        };

        this.createNew = this.createNew.bind(this);
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
            let button = document.createElement("button");
            button.type = "button";
            button.innerText = "Details";
            button.onclick = function () {
                window.location.href = "/contract/" + element.id;
            };
            actionTdTag.append(button);
            row.append(actionTdTag);

            overviewTable.append(row);
        }
    }

    createNew() {
        window.location.href = "/new";
    }

    render() {
        return (
            <>
                <div className="custom-flex">
                    <h1>Overview</h1>
                    <button type="button" onClick={this.createNew}>Create new</button>
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