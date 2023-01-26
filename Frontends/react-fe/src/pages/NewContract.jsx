import React from 'react';
import axios from "axios";

class NewContract extends React.Component {

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

        this.saveContract = this.saveContract.bind(this);
        this.discardContract = this.discardContract.bind(this);
        this.handleNumberChange = this.handleNumberChange.bind(this);

        this.inputName = React.createRef();
        this.inputCategory = React.createRef();
        this.inputExpenses = React.createRef();
        this.inputCustomerNr = React.createRef();
        this.inputContractNr = React.createRef();
        this.inputStartDate = React.createRef();
        this.inputContractPeriod = React.createRef();
        this.inputPeriodOfNotice = React.createRef();
        this.inputCycle = React.createRef();
        this.inputDescription = React.createRef();
        this.inputDocumentPath = React.createRef();
    }

    componentDidMount() {
        // DoNothing
    }

    saveContract() {
        let newContract = {
            category: this.inputCategory.current.value,
            name: this.inputName.current.value,
            expenses: this.inputExpenses.current.value,
            cycle: this.inputCycle.current.value,
            customerNr: this.inputCustomerNr.current.value,
            contractNr: this.inputContractNr.current.value,
            startDate: this.inputStartDate.current.value,
            contractPeriod: this.inputContractPeriod.current.value,
            periodOfNotice: this.inputPeriodOfNotice.current.value,
            description: this.inputDescription.current.value,
            documentPath: this.inputDocumentPath.current.value
        };

        console.log("newContract", newContract);

        axios.post("http://localhost:8080/add", newContract)
            .then(response => {
                if (response.status === 400)
                    throw(new Error("General error -- see backend logs"));
                window.location.href = "/home";
            })
            .catch(error => {
                console.error("Adding contract failed.", error);
                alert("Something went wrong, please check the inputs or the backend logs.")
            });
    }

    discardContract() {
        window.location.href = "/home";
    }

    handleNumberChange(event) {
        event.preventDefault();
        console.log("numchange", event.target.id);
        console.log("state", this.state);
        switch (event.target.id) {
            case "inputExpenses": this.setState({expenses: event.target.value}); break;
            case "inputCycle": this.setState({cycle: event.target.value}); break;
            case "inputContractPeriod": this.setState({contractPeriod: event.target.value}); break;
            case "inputPeriodOfNotice": this.setState({periodOfNotice: event.target.value}); break;
            default:
                console.error("undefined behaviour!");
                alert("undefined behaviour");
        }
    }

    render() {
        let contract = this.state;

        return (
            <>
                <div className="custom-flex">
                    <h1>New contract</h1>
                    <div>
                        <button type="button" onClick={this.discardContract}>Discard</button>
                        <button type="button" onClick={this.saveContract}>Save</button>
                    </div>
                </div>
                <div className="grid-container-2col">
                    <div className="grid-item grid-head">ID</div>
                    <div className="grid-item">0</div>

                    <div className="grid-item grid-head">Name</div>
                    <div className="grid-item">
                        <input type="text" id="inputName" className="editable" ref={this.inputName}
                               defaultValue={contract.name}/>
                    </div>

                    <div className="grid-item grid-head">Category</div>
                    <div className="grid-item">
                        <input type="text" id="inputCategory" className="editable" ref={this.inputCategory}
                               defaultValue={contract.category}/>
                    </div>

                    <div className="grid-item grid-head">Expenses per payment (in &euro;)</div>
                    <div className="grid-item">
                        <input type="number" id="inputExpenses" className="editable" ref={this.inputExpenses}
                               onChange={this.handleNumberChange} value={contract.expenses}/>
                    </div>

                    <div className="grid-item grid-head">Payment cycle</div>
                    <div className="grid-item">
                        <input type="number" id="inputCycle" className="editable" ref={this.inputCycle}
                               onChange={this.handleNumberChange} defaultValue={contract.cycle}/>
                    </div>

                    <div className="grid-item grid-head">Expenses per year</div>
                    <div className="grid-item">{contract.expenses * 12 / contract.cycle} &euro;</div>

                    <div className="grid-item grid-head">Customer number</div>
                    <div className="grid-item">
                        <input type="text" id="inputCustomerNr" className="editable" ref={this.inputCustomerNr}
                               defaultValue={contract.customerNr}/>
                    </div>

                    <div className="grid-item grid-head">Contract number</div>
                    <div className="grid-item">
                        <input type="text" id="inputContractNr" className="editable" ref={this.inputContractNr}
                               defaultValue={contract.contractNr}/>
                    </div>

                    <div className="grid-item grid-head">Start date</div>
                    <div className="grid-item">
                        <input type="date" id="inputStartDate" className="editable" ref={this.inputStartDate}
                               defaultValue={contract.startDate}/>
                    </div>

                    <div className="grid-item grid-head">Contract period (months)</div>
                    <div className="grid-item">
                        <input type="number" id="inputContractPeriod" className="editable" ref={this.inputContractPeriod}
                               onChange={this.handleNumberChange} defaultValue={contract.contractPeriod}/>
                    </div>

                    <div className="grid-item grid-head">Period of Notice (months)</div>
                    <div className="grid-item">
                        <input type="number" id="inputPeriodOfNotice" className="editable" ref={this.inputPeriodOfNotice}
                               onChange={this.handleNumberChange} defaultValue={contract.periodOfNotice}/>
                    </div>

                    <div className="grid-item grid-head">Description</div>
                    <div className="grid-item">
                        <textarea id="inputDescription" className="editable" ref={this.inputDescription}
                               defaultValue={contract.description}/>
                    </div>

                    <div className="grid-item grid-head">Document path</div>
                    <div className="grid-item">
                        <input type="text" id="inputDocumentPath" className="editable" ref={this.inputDocumentPath}
                               defaultValue={contract.documentPath}/>
                    </div>
                </div>
            </>
        )
    }

}

export default NewContract;