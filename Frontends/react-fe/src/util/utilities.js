import axios from "axios";

function objIsEqual(obj1, obj2) {
    if (obj1 === obj2) return;

    if (JSON.stringify(obj1) === JSON.stringify(obj2)) return;

    console.debug("obj1", obj1);
    console.debug("obj2", obj2);

    let differences = [];
    let keys = Object.keys(obj1);

    keys.forEach(keyElement => {
        if (obj1[keyElement] !== obj2[keyElement]) differences.push({[keyElement]: obj2[keyElement]});
    });

    return differences;
}

function deleteContract(id, name) {
    let body = {
        id: id,
        name: name
    };
    let confirmation = window.confirm("Are you sure to delete this contract (ID = " + id + ")?");
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
}

export { objIsEqual, deleteContract }