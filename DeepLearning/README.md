# Deep Learning Intrusion Detection System

## Overview

This project implements a Deep Learning Intrusion Detection System (IDS) using a Generative Adversarial Network (GAN) for unsupervised learning and transfer learning for classification. The system is designed to detect abnormal network activities indicative of cyber intrusions.

## Project Structure

The project is organized into the following main components:

1. **Data Preprocessing**
   - The `preprocess.py` script contains functions to read and preprocess the NSL-KDD dataset.
   - It includes label mapping, normalization, one-hot encoding, and feature selection.

2. **Unsupervised Learning with GANs**
   - The `gan.py` script implements a GAN for unsupervised learning to generate synthetic normal data.
   - The GAN consists of an Encoder, Generator, and Discriminator.

3. **Transfer Learning for Classification**
   - The `transfer_learning.py` script utilizes transfer learning to adapt a pre-trained discriminator for classification.
   - The model architecture includes an Encoder, Pre-trained Discriminator, and additional classification layers.

4. **Training and Evaluation**
   - The `train.py` script trains the GAN and transfer learning model on the NSL-KDD training dataset.
   - Evaluation metrics, such as accuracy, precision, recall, and AUC-ROC, are calculated on the test set.

## Dependencies

- Python 3.x
- PyTorch
- NumPy
- Pandas
- Matplotlib
- Seaborn
- tqdm
- scikit-learn
