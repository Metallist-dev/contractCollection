export default function objIsEqual (obj1, obj2) {
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