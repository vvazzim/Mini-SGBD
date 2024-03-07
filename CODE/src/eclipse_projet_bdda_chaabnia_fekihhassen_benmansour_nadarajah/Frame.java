package eclipse_projet_bdda_chaabnia_fekihhassen_benmansour_nadarajah;

import java.nio.ByteBuffer;

/**
 * Représente une frame utilisé dans le gestionnaire de tampons.
 */
public class Frame {
	private ByteBuffer buffer;
	private int pinCount;
	private boolean dirty;

	/**
	 * Initialise un nouveau frame avec le buffer spécifié.
	 *
	 * @param buffer Le tampon associé à ce frame.
	 */
	public Frame(ByteBuffer buffer) {
		// this.buffer = new byte[bufferSize];
		// this.buffer = new byte[DBParams.SGBDPageSize];
		// this.buffer = ByteBuffer.allocate(DBParams.SGBDPageSize);
		this.buffer = buffer;
		this.pinCount = 1;
		this.dirty = false;
	}

	/**
	 * Incrémente le pin count pour cette frame.
	 */
	public void incrementerPinCount() {
		pinCount++;
	}

	/**
	 * Décrémente le pin count pour cette frame
	 */
	public void decrementerPinCount() {
		if (pinCount > 0) {
			pinCount--;
		} else {
			pinCount = 0;
		}
	}
	/*
	 * public void decrementerPinCount() {
	 * if(pinCount==0) {
	 * break;
	 * }
	 * if(pinCount>0) {
	 * pinCount--;
	 * }
	 * 
	 * }
	 */

	/**
	 * Récupère le tampon associé à cette frame.
	 *
	 * @return Le tampon de ce cadre.
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Récupère le pin count de cette frame.
	 *
	 * @return Le compteur de références (pin count).
	 */
	public int getPinCount() {
		return pinCount;
	}

	/**
	 * Vérifie si ce cadre a été modifié (dirty).
	 *
	 * @return true si le cadre est marqué comme "dirty", false sinon.
	 */
	public boolean getDirty() {
		return dirty;
	}

	/**
	 * Définit le tampon associé à cette frame.
	 *
	 * @param buffer Le nouveau tampon à associer à ce cadre.
	 */
	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * Définit le pin count de cette frame.
	 *
	 * @param pinCount Le nouveau compteur de références (pin count) à définir pour
	 *                 ce cadre.
	 */
	public void setPinCount(int pinCount) {
		this.pinCount = pinCount;
	}

	/**
	 * Définit l'état "dirty" de ce cadre.
	 *
	 * @param dirty true pour marquer le cadre comme dirty false sinon.
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}