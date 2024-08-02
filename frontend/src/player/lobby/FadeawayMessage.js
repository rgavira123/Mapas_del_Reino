import React, { useEffect, useState } from 'react';
import '../../static/css/player/lobby/FadeawayMessage.css'; // Asegúrate de importar tu archivo CSS

export default function FadeawayMessage({ message }) {
    const [visible, setVisible] = useState(true);

    useEffect(() => {
        // Hacer visible el mensaje cada vez que cambia
        setVisible(true);

        // Ocultar el mensaje después de 3 segundos
        const timeoutId = setTimeout(() => setVisible(false), 3000);

        // Limpiar el temporizador si el mensaje cambia antes de que se oculte
        return () => clearTimeout(timeoutId);
    }, [message]);

    if (!visible) {
        return null;
    }

    return <div className="fadeout">{message}</div>;
}