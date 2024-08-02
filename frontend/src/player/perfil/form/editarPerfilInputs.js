import { formValidators } from "../../../validators/formValidators";

export const editarPerfilInputs = [
    {
        tag: "Nombre",
        name: "firstName",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
    {
        tag: "Alias",
        name: "lastName",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
    {
        tag: "E-mail",
        name: "email",
        type: "text",
        defaultValue: "",
        isRequired: true,
        validators: [formValidators.notEmptyValidator],
    },
];