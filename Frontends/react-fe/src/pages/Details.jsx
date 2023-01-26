import React from "react";
import axios from "axios";
import { objIsEqual, deleteContract } from '../util/utilities';

class Details extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            name: "",
            category: "",
            expenses: 0,
            cycle: 0,
            contractNr: "",
            contractPeriod: 0,
            periodOfNotice: 0,
            customerNr: "",
            description: "",
            documentPath: "",
            startDate: ""
        };

        this.backToOverview = this.backToOverview.bind(this);
        this.toggleEdit = this.toggleEdit.bind(this);
        //this.deleteContract = deleteContract.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);

        this.inputName = React.createRef();
        this.inputCategory = React.createRef();
        this.inputExpenses = React.createRef();
        this.inputContractPeriod = React.createRef();
        this.inputPeriodOfNotice = React.createRef();
        this.inputCycle = React.createRef();
        this.inputDescription = React.createRef();
        this.inputDocumentPath = React.createRef();

        this.lastState = React.createRef();
    }

    componentDidMount() {
        let path = window.location.pathname.split('/');
        const contractID = path[path.length - 1];
        this.setState({
            id: contractID
        })
        console.log(contractID);
        axios.get("http://localhost:8080/get/" + contractID)
            .then(response => {
                console.log("full contract", response.data);
                let contract = response.data.body;
                this.setState({
                    editActive: false,
                    id: contract.id,
                    name: contract.name,
                    category: contract.category,
                    expenses: contract.expenses,
                    cycle: contract.cycle,
                    contractNr: contract.contractNr,
                    contractPeriod: contract.contractPeriod,
                    periodOfNotice: contract.periodOfNotice,
                    customerNr: contract.customerNr,
                    description: contract.description,
                    documentPath: contract.documentPath,
                    startDate: contract.startDate
                })
            });
    }

    toggleEdit(event) {
        let lastStatus = this.state.editActive;
        let fields = document.getElementsByClassName("editable");
        document.getElementById("discardButton").classList.toggle("hidden");

        console.log(fields);

        this.setState({editActive: !lastStatus});

        for (let field of fields) {
            field.classList.toggle("hidden");
        }

        if (!lastStatus) {
            document.getElementById("editButton").innerText = "Save";
            this.lastState = this.state;
        } else {

            document.getElementById("editButton").innerText = "Edit";

            if (event.target.id === "discardButton") return;

            for (let field of fields) {
                let inputValue;
                if (field.tagName === "INPUT") {
                    inputValue = field.value;

                    switch (field.id) {
                        case "inputName":
                            this.setState({name: inputValue});
                            break;
                        case "inputCategory":
                            this.setState({category: inputValue});
                            break;
                        case "inputExpenses":
                            console.log(inputValue);
                            this.setState({expenses: inputValue});
                            break;
                        case "inputCycle":
                            this.setState({cycle: inputValue});
                            break;
                        case "inputContractPeriod":
                            this.setState({contractPeriod: inputValue});
                            break;
                        case "inputPeriodofNotice":
                            this.setState({periodOfNotice: inputValue});
                            break;
                        case "inputDescription":
                            this.setState({description: inputValue});
                            break;
                        case "inputDocumentPath":
                            this.setState({documentPath: inputValue});
                            break;
                        default:
                            alert("Something went wrong: field name " + field.id + " unknown.")
                    }
                }
            }

            let diffs = objIsEqual(this.lastState, this.state);
            console.log("diffs", diffs);
            let changes = Object.keys(diffs);
            console.log("keys", changes);
            changes.forEach(changeElement => {
                let keyname = Object.keys(diffs[changeElement])[0];
                if (keyname !== "editActive") {
                    let body = {
                        key: keyname,
                        value: diffs[changeElement][keyname]
                    };
                    console.debug("body", body);
                    let id = this.state.id;

                    axios.put("http://localhost:8080/change/" + id, body)
                        .then(response => {
                            console.log(response);
                        })
                }
            })
        }
    }

    backToOverview() {
        axios.get("http://localhost:8080/all")
            .then(response => {
                console.log(response);
                window.location.href = "/home";
            })
            .catch(error => {
                console.error(error);
                alert(error);
            });
    }

    /*deleteContract() {
        let body = {
            id: this.state.id,
            name: this.state.name
        };
        let confirmation = window.confirm("Are you sure to delete this contract?");
        if (!confirmation) return;
        console.log("deleting contract ", body);
        axios.post("http://localhost:8080/delete", body)
            .then(response => {
                if (response.status === 404)
                    throw new Error("Contract missing in backend.");
                if (response.status === 500)
                    throw new Error("General backend error.");

                window.location.href = "/home";
            })
            .catch(error => {
                console.error("Some error occurred: ", error);
                alert("Something went wrong!");
            });
    }*/

    handleInputChange(event) {
        event.preventDefault();
        console.log("input change", event.target.id);
        console.log("state", this.state);
        switch (event.target.id) {
            case "inputExpenses": this.setState({expenses: event.target.value}); break;
            case "inputCycle": this.setState({cycle: event.target.value}); break;
            case "inputContractPeriod": this.setState({contractPeriod: event.target.value}); break;
            case "inputPeriodOfNotice": this.setState({periodOfNotice: event.target.value}); break;
            case "inputDescription": this.setState({description: event.target.value}); break;
            case "inputDocumentPath": this.setState({documentPath: event.target.value}); break;
            default:
                console.error("undefined behaviour!");
                alert("undefined behaviour");
        }
    }

    render() {
        let contract = this.state;
        let editable = this.state.editActive;
        console.log(contract);
        console.log(editable);

        this.inputName = contract.name;
        this.inputCategory = contract.category;
        this.inputExpenses = contract.expenses;
        this.inputDescription = contract.description;
        this.inputDocumentPath = contract.documentPath;

        return (
            <>
                <div className="custom-flex">
                    <h1>Details contract #{contract.id} - {contract.name}</h1>
                    <div>
                        <button type="button" id="backButton" onClick={this.backToOverview}>Overview</button>
                        <button type="button" id="discardButton" className="hidden" onClick={this.toggleEdit}>Discard</button>
                        <button type="button" id="editButton" onClick={this.toggleEdit}>Edit</button>
                        <button type="button" id="deleteButton" onClick={() => { deleteContract(contract.id, contract.name); }}>Delete</button>
                    </div>
                </div>
                <h2>Basic data</h2>
                <div className="grid-container-4col">
                    <div className="grid-item grid-head">Name</div>
                    <div className="grid-item grid-head">Category</div>
                    <div className="grid-item grid-head">Expenses per payment</div>
                    <div className="grid-item grid-head">Expenses per year</div>
                    <div className="grid-item">
                        <div className="editable">{contract.name}</div>
                        <input type="text" id="inputName" className="editable hidden" ref={this.inputName}
                               defaultValue={contract.name}/>
                    </div>
                    <div className="grid-item">
                        <div className="editable">{contract.category}</div>
                        <input type="text" id="inputCategory" className="editable hidden" ref={this.inputCategory}
                               defaultValue={contract.category}/>
                    </div>
                    <div className="grid-item">
                        <div className="editable">{contract.expenses} &euro;</div>
                        <input type="number" id="inputExpenses" className="editable hidden" ref={this.inputExpenses}
                               onChange={this.handleInputChange} value={contract.expenses}/>
                    </div>
                    <div className="grid-item">{contract.expenses * (12 / contract.cycle)} &euro;</div>
                </div>

                <p>&nbsp;</p>

                <h2>Further information</h2>
                <table id="detailTable">
                    <tr>
                        <th>Payment cycle</th>
                        <td className="editable">{contract.cycle}</td>
                        <td className="editable hidden">
                            <input type="number" id="inputCycle" className="editable hidden" ref={this.inputCycle}
                                   onChange={this.handleInputChange} value={contract.cycle}/>
                        </td>
                    </tr>
                    <tr>
                        <th>Customer number</th>
                        <td className="">{contract.customerNr}</td>
                    </tr>
                    <tr>
                        <th>Contract number</th>
                        <td className="">{contract.contractNr}</td>
                    </tr>
                    <tr>
                        <th>Start date</th>
                        <td className="">
                            {String(new Date(contract.startDate).getDate()).padStart(2, '0')}.
                            {String(new Date(contract.startDate).getMonth() + 1).padStart(2, '0')}.
                            {new Date(contract.startDate).getFullYear()}
                        </td>
                    </tr>
                    <tr>
                        <th>Contract period</th>
                        <td className="editable">{contract.contractPeriod}</td>
                        <td className="editable hidden">
                            <input type="number" id="inputContractPeriod" className="editable hidden" ref={this.inputContractPeriod}
                                   onChange={this.handleInputChange} value={contract.contractPeriod}/>
                        </td>
                    </tr>
                    <tr>
                        <th>Period of notice</th>
                        <td className="editable">{contract.periodOfNotice}</td>
                        <td className="editable hidden">
                            <input type="number" id="inputPeriodOfNotice" className="editable hidden" ref={this.inputPeriodOfNotice}
                                   onChange={this.handleInputChange} value={contract.periodOfNotice}/>
                        </td>
                    </tr>
                    <tr>
                        <th>Description</th>
                        <td className="editable">{contract.description}</td>
                        <td className="editable hidden">
                            <input type="text" id="inputDescription" className="editable hidden" ref={this.inputDescription}
                                   onChange={this.handleInputChange} value={contract.description}/>
                        </td>
                    </tr>
                    <tr>
                        <th>Document Path</th>
                        <td className="editable">{contract.documentPath}</td>
                        <td className="editable hidden">
                            <input type="text" id="inputDocumentPath" className="editable hidden" ref={this.inputDocumentPath}
                                   onChange={this.handleInputChange} value={contract.documentPath}/>
                        </td>
                    </tr>

                </table>
            </>
        );
    }
}

export default Details;