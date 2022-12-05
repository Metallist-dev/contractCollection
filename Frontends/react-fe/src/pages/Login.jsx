import React from 'react';
import axios from "axios";

class Login extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            uploadedFile: null,
            password: ""
        }
        this.handleFileChange = this.handleFileChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
    }

    handleFileChange(event) {
        let file = event.target.files[0];
        console.log(file);

        const reader = new FileReader();

        reader.addEventListener("load", () => {
            this.setState({
                uploadedFile: reader.result,
                password: this.state.password
            });
            console.log("I've read something");
        }, false);

        if (file) {
            reader.readAsText(file);
        }
    }

    handleSubmit() {
        console.log("Submit button clicked.");

        let requestBody = {
            content: this.state.uploadedFile,
            password: this.state.password,
            overwrite: true
        };

        console.log(this.state.uploadedFile);
        console.log(requestBody);

        axios.put("http://localhost:8080/import", requestBody)
            .then(response => {
                console.log(response)
                window.location.href = "/home";
            })
            .catch(error => {
                console.error(error)
                alert(error);
            });
    }

    handlePasswordChange(event) {
        event.preventDefault();
        console.log(event.target.value);
        this.setState({
            uploadedFile: this.state.uploadedFile,
            password: event.target.value
        })
    }

    render() {
        return (
            <div className="login">
                <h1>Login Page</h1>
                <form onSubmit={this.handleSubmit}>
                    <label>File location:
                        <input type="file" id="fileUploader" onChange={this.handleFileChange} />
                    </label>
                    <label>Password:
                        <input type="password" onChange={this.handlePasswordChange}/>
                    </label>
                    <button type="button" onClick={this.handleSubmit}>Submit</button>
                </form>
            </div>
        )
    }
}

export default Login;