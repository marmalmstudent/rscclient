import numpy as np
import os

landscape_path = "../cache/Landscape/"

landscape0_file = [
    ['h0x48y37', 'h0x48y38', 'h0x48y39', 'h0x48y40', 'h0x48y41', 'h0x48y42',
     'h0x48y43', 'h0x48y44', 'h0x48y45', 'h0x48y46', 'h0x48y47', 'h0x48y48',
     'h0x48y49', 'h0x48y50', 'h0x48y51', 'h0x48y52', 'h0x48y53', 'h0x48y54',
     'h0x48y55', 'h0x48y56', 'h0x48y57'],
    ['h0x49y37', 'h0x49y38', 'h0x49y39', 'h0x49y40', 'h0x49y41', 'h0x49y42',
     'h0x49y43', 'h0x49y44', 'h0x49y45', 'h0x49y46', 'h0x49y47', 'h0x49y48',
     'h0x49y49', 'h0x49y50', 'h0x49y51', 'h0x49y52', 'h0x49y53', 'h0x49y54',
     'h0x49y55', 'h0x49y56', 'h0x49y57'],
    ['h0x50y37', 'h0x50y38', 'h0x50y39', 'h0x50y40', 'h0x50y41', 'h0x50y42',
     'h0x50y43', 'h0x50y44', 'h0x50y45', 'h0x50y46', 'h0x50y47', 'h0x50y48',
     'h0x50y49', 'h0x50y50', 'h0x50y51', 'h0x50y52', 'h0x50y53', 'h0x50y54',
     'h0x50y55', 'h0x50y56', 'h0x50y57'],
    ['h0x51y37', 'h0x51y38', 'h0x51y39', 'h0x51y40', 'h0x51y41', 'h0x51y42',
     'h0x51y43', 'h0x51y44', 'h0x51y45', 'h0x51y46', 'h0x51y47', 'h0x51y48',
     'h0x51y49', 'h0x51y50', 'h0x51y51', 'h0x51y52', 'h0x51y53', 'h0x51y54',
     'h0x51y55', 'h0x51y56', 'h0x51y57'],
    ['h0x52y37', 'h0x52y38', 'h0x52y39', 'h0x52y40', 'h0x52y41', 'h0x52y42',
     'h0x52y43', 'h0x52y44', 'h0x52y45', 'h0x52y46', 'h0x52y47', 'h0x52y48',
     'h0x52y49', 'h0x52y50', 'h0x52y51', 'h0x52y52', 'h0x52y53', 'h0x52y54',
     'h0x52y55', 'h0x52y56', 'h0x52y57'],
    ['h0x53y37', 'h0x53y38', 'h0x53y39', 'h0x53y40', 'h0x53y41', 'h0x53y42',
     'h0x53y43', 'h0x53y44', 'h0x53y45', 'h0x53y46', 'h0x53y47', 'h0x53y48',
     'h0x53y49', 'h0x53y50', 'h0x53y51', 'h0x53y52', 'h0x53y53', 'h0x53y54',
     'h0x53y55', 'h0x53y56', 'h0x53y57'],
    ['h0x54y37', 'h0x54y38', 'h0x54y39', 'h0x54y40', 'h0x54y41', 'h0x54y42',
     'h0x54y43', 'h0x54y44', 'h0x54y45', 'h0x54y46', 'h0x54y47', 'h0x54y48',
     'h0x54y49', 'h0x54y50', 'h0x54y51', 'h0x54y52', 'h0x54y53', 'h0x54y54',
     'h0x54y55', 'h0x54y56', 'h0x54y57'],
    ['h0x55y37', 'h0x55y38', 'h0x55y39', 'h0x55y40', 'h0x55y41', 'h0x55y42',
     'h0x55y43', 'h0x55y44', 'h0x55y45', 'h0x55y46', 'h0x55y47', 'h0x55y48',
     'h0x55y49', 'h0x55y50', 'h0x55y51', 'h0x55y52', 'h0x55y53', 'h0x55y54',
     'h0x55y55', 'h0x55y56', 'h0x55y57'],
    ['h0x56y37', 'h0x56y38', 'h0x56y39', 'h0x56y40', 'h0x56y41', 'h0x56y42',
     'h0x56y43', 'h0x56y44', 'h0x56y45', 'h0x56y46', 'h0x56y47', 'h0x56y48',
     'h0x56y49', 'h0x56y50', 'h0x56y51', 'h0x56y52', 'h0x56y53', 'h0x56y54',
     'h0x56y55', 'h0x56y56', 'h0x56y57'],
    ['h0x57y37', 'h0x57y38', 'h0x57y39', 'h0x57y40', 'h0x57y41', 'h0x57y42',
     'h0x57y43', 'h0x57y44', 'h0x57y45', 'h0x57y46', 'h0x57y47', 'h0x57y48',
     'h0x57y49', 'h0x57y50', 'h0x57y51', 'h0x57y52', 'h0x57y53', 'h0x57y54',
     'h0x57y55', 'h0x57y56', 'h0x57y57'],
    ['h0x58y37', 'h0x58y38', 'h0x58y39', 'h0x58y40', 'h0x58y41', 'h0x58y42',
     'h0x58y43', 'h0x58y44', 'h0x58y45', 'h0x58y46', 'h0x58y47', 'h0x58y48',
     'h0x58y49', 'h0x58y50', 'h0x58y51', 'h0x58y52', 'h0x58y53', 'h0x58y54',
     'h0x58y55', 'h0x58y56', 'h0x58y57'],
    ['h0x59y37', 'h0x59y38', 'h0x59y39', 'h0x59y40', 'h0x59y41', 'h0x59y42',
     'h0x59y43', 'h0x59y44', 'h0x59y45', 'h0x59y46', 'h0x59y47', 'h0x59y48',
     'h0x59y49', 'h0x59y50', 'h0x59y51', 'h0x59y52', 'h0x59y53', 'h0x59y54',
     'h0x59y55', 'h0x59y56', 'h0x59y57'],
    ['h0x60y37', 'h0x60y38', 'h0x60y39', 'h0x60y40', 'h0x60y41', 'h0x60y42',
     'h0x60y43', 'h0x60y44', 'h0x60y45', 'h0x60y46', 'h0x60y47', 'h0x60y48',
     'h0x60y49', 'h0x60y50', 'h0x60y51', 'h0x60y52', 'h0x60y53', 'h0x60y54',
     'h0x60y55', 'h0x60y56', 'h0x60y57'],
    ['h0x61y37', 'h0x61y38', 'h0x61y39', 'h0x61y40', 'h0x61y41', 'h0x61y42',
     'h0x61y43', 'h0x61y44', 'h0x61y45', 'h0x61y46', 'h0x61y47', 'h0x61y48',
     'h0x61y49', 'h0x61y50', 'h0x61y51', 'h0x61y52', 'h0x61y53', 'h0x61y54',
     'h0x61y55', 'h0x61y56', 'h0x61y57'],
    ['h0x62y37', 'h0x62y38', 'h0x62y39', 'h0x62y40', 'h0x62y41', 'h0x62y42',
     'h0x62y43', 'h0x62y44', 'h0x62y45', 'h0x62y46', 'h0x62y47', 'h0x62y48',
     'h0x62y49', 'h0x62y50', 'h0x62y51', 'h0x62y52', 'h0x62y53', 'h0x62y54',
     'h0x62y55', 'h0x62y56', 'h0x62y57'],
    ['h0x63y37', 'h0x63y38', 'h0x63y39', 'h0x63y40', 'h0x63y41', 'h0x63y42',
     'h0x63y43', 'h0x63y44', 'h0x63y45', 'h0x63y46', 'h0x63y47', 'h0x63y48',
     'h0x63y49', 'h0x63y50', 'h0x63y51', 'h0x63y52', 'h0x63y53', 'h0x63y54',
     'h0x63y55', 'h0x63y56', 'h0x63y57'],
    ['h0x64y37', 'h0x64y38', 'h0x64y39', 'h0x64y40', 'h0x64y41', 'h0x64y42',
     'h0x64y43', 'h0x64y44', 'h0x64y45', 'h0x64y46', 'h0x64y47', 'h0x64y48',
     'h0x64y49', 'h0x64y50', 'h0x64y51', 'h0x64y52', 'h0x64y53', 'h0x64y54',
     'h0x64y55', 'h0x64y56', 'h0x64y57'],
    ['h0x65y37', 'h0x65y38', 'h0x65y39', 'h0x65y40', 'h0x65y41', 'h0x65y42',
     'h0x65y43', 'h0x65y44', 'h0x65y45', 'h0x65y46', 'h0x65y47', 'h0x65y48',
     'h0x65y49', 'h0x65y50', 'h0x65y51', 'h0x65y52', 'h0x65y53', 'h0x65y54',
     'h0x65y55', 'h0x65y56', 'h0x65y57'],
    ['h0x66y37', 'h0x66y38', 'h0x66y39', 'h0x66y40', 'h0x66y41', 'h0x66y42',
     'h0x66y43', 'h0x66y44', 'h0x66y45', 'h0x66y46', 'h0x66y47', 'h0x66y48',
     'h0x66y49', 'h0x66y50', 'h0x66y51', 'h0x66y52', 'h0x66y53', 'h0x66y54',
     'h0x66y55', 'h0x66y56', 'h0x66y57'],
    ['h0x67y37', 'h0x67y38', 'h0x67y39', 'h0x67y40', 'h0x67y41', 'h0x67y42',
     'h0x67y43', 'h0x67y44', 'h0x67y45', 'h0x67y46', 'h0x67y47', 'h0x67y48',
     'h0x67y49', 'h0x67y50', 'h0x67y51', 'h0x67y52', 'h0x67y53', 'h0x67y54',
     'h0x67y55', 'h0x67y56', 'h0x67y57'],
    ['h0x68y37', 'h0x68y38', 'h0x68y39', 'h0x68y40', 'h0x68y41', 'h0x68y42',
     'h0x68y43', 'h0x68y44', 'h0x68y45', 'h0x68y46', 'h0x68y47', 'h0x68y48',
     'h0x68y49', 'h0x68y50', 'h0x68y51', 'h0x68y52', 'h0x68y53', 'h0x68y54',
     'h0x68y55', 'h0x68y56', 'h0x68y57']]
landscape1_file = [
    ['h1x48y37', 'h1x48y38', 'h1x48y39', 'h1x48y40', 'h1x48y41', 'h1x48y42',
     'h1x48y43', 'h1x48y44', 'h1x48y45', 'h1x48y46', 'h1x48y47', 'h1x48y48',
     'h1x48y49', 'h1x48y50', 'h1x48y51', 'h1x48y52', 'h1x48y53', 'h1x48y54',
     'h1x48y55', 'h1x48y56', 'h1x48y57'],
    ['h1x49y37', 'h1x49y38', 'h1x49y39', 'h1x49y40', 'h1x49y41', 'h1x49y42',
     'h1x49y43', 'h1x49y44', 'h1x49y45', 'h1x49y46', 'h1x49y47', 'h1x49y48',
     'h1x49y49', 'h1x49y50', 'h1x49y51', 'h1x49y52', 'h1x49y53', 'h1x49y54',
     'h1x49y55', 'h1x49y56', 'h1x49y57'],
    ['h1x50y37', 'h1x50y38', 'h1x50y39', 'h1x50y40', 'h1x50y41', 'h1x50y42',
     'h1x50y43', 'h1x50y44', 'h1x50y45', 'h1x50y46', 'h1x50y47', 'h1x50y48',
     'h1x50y49', 'h1x50y50', 'h1x50y51', 'h1x50y52', 'h1x50y53', 'h1x50y54',
     'h1x50y55', 'h1x50y56', 'h1x50y57'],
    ['h1x51y37', 'h1x51y38', 'h1x51y39', 'h1x51y40', 'h1x51y41', 'h1x51y42',
     'h1x51y43', 'h1x51y44', 'h1x51y45', 'h1x51y46', 'h1x51y47', 'h1x51y48',
     'h1x51y49', 'h1x51y50', 'h1x51y51', 'h1x51y52', 'h1x51y53', 'h1x51y54',
     'h1x51y55', 'h1x51y56', 'h1x51y57'],
    ['h1x52y37', 'h1x52y38', 'h1x52y39', 'h1x52y40', 'h1x52y41', 'h1x52y42',
     'h1x52y43', 'h1x52y44', 'h1x52y45', 'h1x52y46', 'h1x52y47', 'h1x52y48',
     'h1x52y49', 'h1x52y50', 'h1x52y51', 'h1x52y52', 'h1x52y53', 'h1x52y54',
     'h1x52y55', 'h1x52y56', 'h1x52y57'],
    ['h1x53y37', 'h1x53y38', 'h1x53y39', 'h1x53y40', 'h1x53y41', 'h1x53y42',
     'h1x53y43', 'h1x53y44', 'h1x53y45', 'h1x53y46', 'h1x53y47', 'h1x53y48',
     'h1x53y49', 'h1x53y50', 'h1x53y51', 'h1x53y52', 'h1x53y53', 'h1x53y54',
     'h1x53y55', 'h1x53y56', 'h1x53y57'],
    ['h1x54y37', 'h1x54y38', 'h1x54y39', 'h1x54y40', 'h1x54y41', 'h1x54y42',
     'h1x54y43', 'h1x54y44', 'h1x54y45', 'h1x54y46', 'h1x54y47', 'h1x54y48',
     'h1x54y49', 'h1x54y50', 'h1x54y51', 'h1x54y52', 'h1x54y53', 'h1x54y54',
     'h1x54y55', 'h1x54y56', 'h1x54y57'],
    ['h1x55y37', 'h1x55y38', 'h1x55y39', 'h1x55y40', 'h1x55y41', 'h1x55y42',
     'h1x55y43', 'h1x55y44', 'h1x55y45', 'h1x55y46', 'h1x55y47', 'h1x55y48',
     'h1x55y49', 'h1x55y50', 'h1x55y51', 'h1x55y52', 'h1x55y53', 'h1x55y54',
     'h1x55y55', 'h1x55y56', 'h1x55y57'],
    ['h1x56y37', 'h1x56y38', 'h1x56y39', 'h1x56y40', 'h1x56y41', 'h1x56y42',
     'h1x56y43', 'h1x56y44', 'h1x56y45', 'h1x56y46', 'h1x56y47', 'h1x56y48',
     'h1x56y49', 'h1x56y50', 'h1x56y51', 'h1x56y52', 'h1x56y53', 'h1x56y54',
     'h1x56y55', 'h1x56y56', 'h1x56y57'],
    ['h1x57y37', 'h1x57y38', 'h1x57y39', 'h1x57y40', 'h1x57y41', 'h1x57y42',
     'h1x57y43', 'h1x57y44', 'h1x57y45', 'h1x57y46', 'h1x57y47', 'h1x57y48',
     'h1x57y49', 'h1x57y50', 'h1x57y51', 'h1x57y52', 'h1x57y53', 'h1x57y54',
     'h1x57y55', 'h1x57y56', 'h1x57y57'],
    ['h1x58y37', 'h1x58y38', 'h1x58y39', 'h1x58y40', 'h1x58y41', 'h1x58y42',
     'h1x58y43', 'h1x58y44', 'h1x58y45', 'h1x58y46', 'h1x58y47', 'h1x58y48',
     'h1x58y49', 'h1x58y50', 'h1x58y51', 'h1x58y52', 'h1x58y53', 'h1x58y54',
     'h1x58y55', 'h1x58y56', 'h1x58y57'],
    ['h1x59y37', 'h1x59y38', 'h1x59y39', 'h1x59y40', 'h1x59y41', 'h1x59y42',
     'h1x59y43', 'h1x59y44', 'h1x59y45', 'h1x59y46', 'h1x59y47', 'h1x59y48',
     'h1x59y49', 'h1x59y50', 'h1x59y51', 'h1x59y52', 'h1x59y53', 'h1x59y54',
     'h1x59y55', 'h1x59y56', 'h1x59y57'],
    ['h1x60y37', 'h1x60y38', 'h1x60y39', 'h1x60y40', 'h1x60y41', 'h1x60y42',
     'h1x60y43', 'h1x60y44', 'h1x60y45', 'h1x60y46', 'h1x60y47', 'h1x60y48',
     'h1x60y49', 'h1x60y50', 'h1x60y51', 'h1x60y52', 'h1x60y53', 'h1x60y54',
     'h1x60y55', 'h1x60y56', 'h1x60y57'],
    ['h1x61y37', 'h1x61y38', 'h1x61y39', 'h1x61y40', 'h1x61y41', 'h1x61y42',
     'h1x61y43', 'h1x61y44', 'h1x61y45', 'h1x61y46', 'h1x61y47', 'h1x61y48',
     'h1x61y49', 'h1x61y50', 'h1x61y51', 'h1x61y52', 'h1x61y53', 'h1x61y54',
     'h1x61y55', 'h1x61y56', 'h1x61y57'],
    ['h1x62y37', 'h1x62y38', 'h1x62y39', 'h1x62y40', 'h1x62y41', 'h1x62y42',
     'h1x62y43', 'h1x62y44', 'h1x62y45', 'h1x62y46', 'h1x62y47', 'h1x62y48',
     'h1x62y49', 'h1x62y50', 'h1x62y51', 'h1x62y52', 'h1x62y53', 'h1x62y54',
     'h1x62y55', 'h1x62y56', 'h1x62y57'],
    ['h1x63y37', 'h1x63y38', 'h1x63y39', 'h1x63y40', 'h1x63y41', 'h1x63y42',
     'h1x63y43', 'h1x63y44', 'h1x63y45', 'h1x63y46', 'h1x63y47', 'h1x63y48',
     'h1x63y49', 'h1x63y50', 'h1x63y51', 'h1x63y52', 'h1x63y53', 'h1x63y54',
     'h1x63y55', 'h1x63y56', 'h1x63y57'],
    ['h1x64y37', 'h1x64y38', 'h1x64y39', 'h1x64y40', 'h1x64y41', 'h1x64y42',
     'h1x64y43', 'h1x64y44', 'h1x64y45', 'h1x64y46', 'h1x64y47', 'h1x64y48',
     'h1x64y49', 'h1x64y50', 'h1x64y51', 'h1x64y52', 'h1x64y53', 'h1x64y54',
     'h1x64y55', 'h1x64y56', 'h1x64y57'],
    ['h1x65y37', 'h1x65y38', 'h1x65y39', 'h1x65y40', 'h1x65y41', 'h1x65y42',
     'h1x65y43', 'h1x65y44', 'h1x65y45', 'h1x65y46', 'h1x65y47', 'h1x65y48',
     'h1x65y49', 'h1x65y50', 'h1x65y51', 'h1x65y52', 'h1x65y53', 'h1x65y54',
     'h1x65y55', 'h1x65y56', 'h1x65y57'],
    ['h1x66y37', 'h1x66y38', 'h1x66y39', 'h1x66y40', 'h1x66y41', 'h1x66y42',
     'h1x66y43', 'h1x66y44', 'h1x66y45', 'h1x66y46', 'h1x66y47', 'h1x66y48',
     'h1x66y49', 'h1x66y50', 'h1x66y51', 'h1x66y52', 'h1x66y53', 'h1x66y54',
     'h1x66y55', 'h1x66y56', 'h1x66y57'],
    ['h1x67y37', 'h1x67y38', 'h1x67y39', 'h1x67y40', 'h1x67y41', 'h1x67y42',
     'h1x67y43', 'h1x67y44', 'h1x67y45', 'h1x67y46', 'h1x67y47', 'h1x67y48',
     'h1x67y49', 'h1x67y50', 'h1x67y51', 'h1x67y52', 'h1x67y53', 'h1x67y54',
     'h1x67y55', 'h1x67y56', 'h1x67y57'],
    ['h1x68y37', 'h1x68y38', 'h1x68y39', 'h1x68y40', 'h1x68y41', 'h1x68y42',
     'h1x68y43', 'h1x68y44', 'h1x68y45', 'h1x68y46', 'h1x68y47', 'h1x68y48',
     'h1x68y49', 'h1x68y50', 'h1x68y51', 'h1x68y52', 'h1x68y53', 'h1x68y54',
     'h1x68y55', 'h1x68y56', 'h1x68y57']]

landscape2_file = [
    ['h2x48y37', 'h2x48y38', 'h2x48y39', 'h2x48y40', 'h2x48y41', 'h2x48y42',
     'h2x48y43', 'h2x48y44', 'h2x48y45', 'h2x48y46', 'h2x48y47', 'h2x48y48',
     'h2x48y49', 'h2x48y50', 'h2x48y51', 'h2x48y52', 'h2x48y53', 'h2x48y54',
     'h2x48y55', 'h2x48y56', 'h2x48y57'],
    ['h2x49y37', 'h2x49y38', 'h2x49y39', 'h2x49y40', 'h2x49y41', 'h2x49y42',
     'h2x49y43', 'h2x49y44', 'h2x49y45', 'h2x49y46', 'h2x49y47', 'h2x49y48',
     'h2x49y49', 'h2x49y50', 'h2x49y51', 'h2x49y52', 'h2x49y53', 'h2x49y54',
     'h2x49y55', 'h2x49y56', 'h2x49y57'],
    ['h2x50y37', 'h2x50y38', 'h2x50y39', 'h2x50y40', 'h2x50y41', 'h2x50y42',
     'h2x50y43', 'h2x50y44', 'h2x50y45', 'h2x50y46', 'h2x50y47', 'h2x50y48',
     'h2x50y49', 'h2x50y50', 'h2x50y51', 'h2x50y52', 'h2x50y53', 'h2x50y54',
     'h2x50y55', 'h2x50y56', 'h2x50y57'],
    ['h2x51y37', 'h2x51y38', 'h2x51y39', 'h2x51y40', 'h2x51y41', 'h2x51y42',
     'h2x51y43', 'h2x51y44', 'h2x51y45', 'h2x51y46', 'h2x51y47', 'h2x51y48',
     'h2x51y49', 'h2x51y50', 'h2x51y51', 'h2x51y52', 'h2x51y53', 'h2x51y54',
     'h2x51y55', 'h2x51y56', 'h2x51y57'],
    ['h2x52y37', 'h2x52y38', 'h2x52y39', 'h2x52y40', 'h2x52y41', 'h2x52y42',
     'h2x52y43', 'h2x52y44', 'h2x52y45', 'h2x52y46', 'h2x52y47', 'h2x52y48',
     'h2x52y49', 'h2x52y50', 'h2x52y51', 'h2x52y52', 'h2x52y53', 'h2x52y54',
     'h2x52y55', 'h2x52y56', 'h2x52y57'],
    ['h2x53y37', 'h2x53y38', 'h2x53y39', 'h2x53y40', 'h2x53y41', 'h2x53y42',
     'h2x53y43', 'h2x53y44', 'h2x53y45', 'h2x53y46', 'h2x53y47', 'h2x53y48',
     'h2x53y49', 'h2x53y50', 'h2x53y51', 'h2x53y52', 'h2x53y53', 'h2x53y54',
     'h2x53y55', 'h2x53y56', 'h2x53y57'],
    ['h2x54y37', 'h2x54y38', 'h2x54y39', 'h2x54y40', 'h2x54y41', 'h2x54y42',
     'h2x54y43', 'h2x54y44', 'h2x54y45', 'h2x54y46', 'h2x54y47', 'h2x54y48',
     'h2x54y49', 'h2x54y50', 'h2x54y51', 'h2x54y52', 'h2x54y53', 'h2x54y54',
     'h2x54y55', 'h2x54y56', 'h2x54y57'],
    ['h2x55y37', 'h2x55y38', 'h2x55y39', 'h2x55y40', 'h2x55y41', 'h2x55y42',
     'h2x55y43', 'h2x55y44', 'h2x55y45', 'h2x55y46', 'h2x55y47', 'h2x55y48',
     'h2x55y49', 'h2x55y50', 'h2x55y51', 'h2x55y52', 'h2x55y53', 'h2x55y54',
     'h2x55y55', 'h2x55y56', 'h2x55y57'],
    ['h2x56y37', 'h2x56y38', 'h2x56y39', 'h2x56y40', 'h2x56y41', 'h2x56y42',
     'h2x56y43', 'h2x56y44', 'h2x56y45', 'h2x56y46', 'h2x56y47', 'h2x56y48',
     'h2x56y49', 'h2x56y50', 'h2x56y51', 'h2x56y52', 'h2x56y53', 'h2x56y54',
     'h2x56y55', 'h2x56y56', 'h2x56y57'],
    ['h2x57y37', 'h2x57y38', 'h2x57y39', 'h2x57y40', 'h2x57y41', 'h2x57y42',
     'h2x57y43', 'h2x57y44', 'h2x57y45', 'h2x57y46', 'h2x57y47', 'h2x57y48',
     'h2x57y49', 'h2x57y50', 'h2x57y51', 'h2x57y52', 'h2x57y53', 'h2x57y54',
     'h2x57y55', 'h2x57y56', 'h2x57y57'],
    ['h2x58y37', 'h2x58y38', 'h2x58y39', 'h2x58y40', 'h2x58y41', 'h2x58y42',
     'h2x58y43', 'h2x58y44', 'h2x58y45', 'h2x58y46', 'h2x58y47', 'h2x58y48',
     'h2x58y49', 'h2x58y50', 'h2x58y51', 'h2x58y52', 'h2x58y53', 'h2x58y54',
     'h2x58y55', 'h2x58y56', 'h2x58y57'],
    ['h2x59y37', 'h2x59y38', 'h2x59y39', 'h2x59y40', 'h2x59y41', 'h2x59y42',
     'h2x59y43', 'h2x59y44', 'h2x59y45', 'h2x59y46', 'h2x59y47', 'h2x59y48',
     'h2x59y49', 'h2x59y50', 'h2x59y51', 'h2x59y52', 'h2x59y53', 'h2x59y54',
     'h2x59y55', 'h2x59y56', 'h2x59y57'],
    ['h2x60y37', 'h2x60y38', 'h2x60y39', 'h2x60y40', 'h2x60y41', 'h2x60y42',
     'h2x60y43', 'h2x60y44', 'h2x60y45', 'h2x60y46', 'h2x60y47', 'h2x60y48',
     'h2x60y49', 'h2x60y50', 'h2x60y51', 'h2x60y52', 'h2x60y53', 'h2x60y54',
     'h2x60y55', 'h2x60y56', 'h2x60y57'],
    ['h2x61y37', 'h2x61y38', 'h2x61y39', 'h2x61y40', 'h2x61y41', 'h2x61y42',
     'h2x61y43', 'h2x61y44', 'h2x61y45', 'h2x61y46', 'h2x61y47', 'h2x61y48',
     'h2x61y49', 'h2x61y50', 'h2x61y51', 'h2x61y52', 'h2x61y53', 'h2x61y54',
     'h2x61y55', 'h2x61y56', 'h2x61y57'],
    ['h2x62y37', 'h2x62y38', 'h2x62y39', 'h2x62y40', 'h2x62y41', 'h2x62y42',
     'h2x62y43', 'h2x62y44', 'h2x62y45', 'h2x62y46', 'h2x62y47', 'h2x62y48',
     'h2x62y49', 'h2x62y50', 'h2x62y51', 'h2x62y52', 'h2x62y53', 'h2x62y54',
     'h2x62y55', 'h2x62y56', 'h2x62y57'],
    ['h2x63y37', 'h2x63y38', 'h2x63y39', 'h2x63y40', 'h2x63y41', 'h2x63y42',
     'h2x63y43', 'h2x63y44', 'h2x63y45', 'h2x63y46', 'h2x63y47', 'h2x63y48',
     'h2x63y49', 'h2x63y50', 'h2x63y51', 'h2x63y52', 'h2x63y53', 'h2x63y54',
     'h2x63y55', 'h2x63y56', 'h2x63y57'],
    ['h2x64y37', 'h2x64y38', 'h2x64y39', 'h2x64y40', 'h2x64y41', 'h2x64y42',
     'h2x64y43', 'h2x64y44', 'h2x64y45', 'h2x64y46', 'h2x64y47', 'h2x64y48',
     'h2x64y49', 'h2x64y50', 'h2x64y51', 'h2x64y52', 'h2x64y53', 'h2x64y54',
     'h2x64y55', 'h2x64y56', 'h2x64y57'],
    ['h2x65y37', 'h2x65y38', 'h2x65y39', 'h2x65y40', 'h2x65y41', 'h2x65y42',
     'h2x65y43', 'h2x65y44', 'h2x65y45', 'h2x65y46', 'h2x65y47', 'h2x65y48',
     'h2x65y49', 'h2x65y50', 'h2x65y51', 'h2x65y52', 'h2x65y53', 'h2x65y54',
     'h2x65y55', 'h2x65y56', 'h2x65y57'],
    ['h2x66y37', 'h2x66y38', 'h2x66y39', 'h2x66y40', 'h2x66y41', 'h2x66y42',
     'h2x66y43', 'h2x66y44', 'h2x66y45', 'h2x66y46', 'h2x66y47', 'h2x66y48',
     'h2x66y49', 'h2x66y50', 'h2x66y51', 'h2x66y52', 'h2x66y53', 'h2x66y54',
     'h2x66y55', 'h2x66y56', 'h2x66y57'],
    ['h2x67y37', 'h2x67y38', 'h2x67y39', 'h2x67y40', 'h2x67y41', 'h2x67y42',
     'h2x67y43', 'h2x67y44', 'h2x67y45', 'h2x67y46', 'h2x67y47', 'h2x67y48',
     'h2x67y49', 'h2x67y50', 'h2x67y51', 'h2x67y52', 'h2x67y53', 'h2x67y54',
     'h2x67y55', 'h2x67y56', 'h2x67y57'],
    ['h2x68y37', 'h2x68y38', 'h2x68y39', 'h2x68y40', 'h2x68y41', 'h2x68y42',
     'h2x68y43', 'h2x68y44', 'h2x68y45', 'h2x68y46', 'h2x68y47', 'h2x68y48',
     'h2x68y49', 'h2x68y50', 'h2x68y51', 'h2x68y52', 'h2x68y53', 'h2x68y54',
     'h2x68y55', 'h2x68y56', 'h2x68y57']]

landscape3_file = [
    ['h3x48y37', 'h3x48y38', 'h3x48y39', 'h3x48y40', 'h3x48y41', 'h3x48y42',
     'h3x48y43', 'h3x48y44', 'h3x48y45', 'h3x48y46', 'h3x48y47', 'h3x48y48',
     'h3x48y49', 'h3x48y50', 'h3x48y51', 'h3x48y52', 'h3x48y53', 'h3x48y54',
     'h3x48y55', 'h3x48y56', 'h3x48y57'],
    ['h3x49y37', 'h3x49y38', 'h3x49y39', 'h3x49y40', 'h3x49y41', 'h3x49y42',
     'h3x49y43', 'h3x49y44', 'h3x49y45', 'h3x49y46', 'h3x49y47', 'h3x49y48',
     'h3x49y49', 'h3x49y50', 'h3x49y51', 'h3x49y52', 'h3x49y53', 'h3x49y54',
     'h3x49y55', 'h3x49y56', 'h3x49y57'],
    ['h3x50y37', 'h3x50y38', 'h3x50y39', 'h3x50y40', 'h3x50y41', 'h3x50y42',
     'h3x50y43', 'h3x50y44', 'h3x50y45', 'h3x50y46', 'h3x50y47', 'h3x50y48',
     'h3x50y49', 'h3x50y50', 'h3x50y51', 'h3x50y52', 'h3x50y53', 'h3x50y54',
     'h3x50y55', 'h3x50y56', 'h3x50y57'],
    ['h3x51y37', 'h3x51y38', 'h3x51y39', 'h3x51y40', 'h3x51y41', 'h3x51y42',
     'h3x51y43', 'h3x51y44', 'h3x51y45', 'h3x51y46', 'h3x51y47', 'h3x51y48',
     'h3x51y49', 'h3x51y50', 'h3x51y51', 'h3x51y52', 'h3x51y53', 'h3x51y54',
     'h3x51y55', 'h3x51y56', 'h3x51y57'],
    ['h3x52y37', 'h3x52y38', 'h3x52y39', 'h3x52y40', 'h3x52y41', 'h3x52y42',
     'h3x52y43', 'h3x52y44', 'h3x52y45', 'h3x52y46', 'h3x52y47', 'h3x52y48',
     'h3x52y49', 'h3x52y50', 'h3x52y51', 'h3x52y52', 'h3x52y53', 'h3x52y54',
     'h3x52y55', 'h3x52y56', 'h3x52y57'],
    ['h3x53y37', 'h3x53y38', 'h3x53y39', 'h3x53y40', 'h3x53y41', 'h3x53y42',
     'h3x53y43', 'h3x53y44', 'h3x53y45', 'h3x53y46', 'h3x53y47', 'h3x53y48',
     'h3x53y49', 'h3x53y50', 'h3x53y51', 'h3x53y52', 'h3x53y53', 'h3x53y54',
     'h3x53y55', 'h3x53y56', 'h3x53y57'],
    ['h3x54y37', 'h3x54y38', 'h3x54y39', 'h3x54y40', 'h3x54y41', 'h3x54y42',
     'h3x54y43', 'h3x54y44', 'h3x54y45', 'h3x54y46', 'h3x54y47', 'h3x54y48',
     'h3x54y49', 'h3x54y50', 'h3x54y51', 'h3x54y52', 'h3x54y53', 'h3x54y54',
     'h3x54y55', 'h3x54y56', 'h3x54y57'],
    ['h3x55y37', 'h3x55y38', 'h3x55y39', 'h3x55y40', 'h3x55y41', 'h3x55y42',
     'h3x55y43', 'h3x55y44', 'h3x55y45', 'h3x55y46', 'h3x55y47', 'h3x55y48',
     'h3x55y49', 'h3x55y50', 'h3x55y51', 'h3x55y52', 'h3x55y53', 'h3x55y54',
     'h3x55y55', 'h3x55y56', 'h3x55y57'],
    ['h3x56y37', 'h3x56y38', 'h3x56y39', 'h3x56y40', 'h3x56y41', 'h3x56y42',
     'h3x56y43', 'h3x56y44', 'h3x56y45', 'h3x56y46', 'h3x56y47', 'h3x56y48',
     'h3x56y49', 'h3x56y50', 'h3x56y51', 'h3x56y52', 'h3x56y53', 'h3x56y54',
     'h3x56y55', 'h3x56y56', 'h3x56y57'],
    ['h3x57y37', 'h3x57y38', 'h3x57y39', 'h3x57y40', 'h3x57y41', 'h3x57y42',
     'h3x57y43', 'h3x57y44', 'h3x57y45', 'h3x57y46', 'h3x57y47', 'h3x57y48',
     'h3x57y49', 'h3x57y50', 'h3x57y51', 'h3x57y52', 'h3x57y53', 'h3x57y54',
     'h3x57y55', 'h3x57y56', 'h3x57y57'],
    ['h3x58y37', 'h3x58y38', 'h3x58y39', 'h3x58y40', 'h3x58y41', 'h3x58y42',
     'h3x58y43', 'h3x58y44', 'h3x58y45', 'h3x58y46', 'h3x58y47', 'h3x58y48',
     'h3x58y49', 'h3x58y50', 'h3x58y51', 'h3x58y52', 'h3x58y53', 'h3x58y54',
     'h3x58y55', 'h3x58y56', 'h3x58y57'],
    ['h3x59y37', 'h3x59y38', 'h3x59y39', 'h3x59y40', 'h3x59y41', 'h3x59y42',
     'h3x59y43', 'h3x59y44', 'h3x59y45', 'h3x59y46', 'h3x59y47', 'h3x59y48',
     'h3x59y49', 'h3x59y50', 'h3x59y51', 'h3x59y52', 'h3x59y53', 'h3x59y54',
     'h3x59y55', 'h3x59y56', 'h3x59y57'],
    ['h3x60y37', 'h3x60y38', 'h3x60y39', 'h3x60y40', 'h3x60y41', 'h3x60y42',
     'h3x60y43', 'h3x60y44', 'h3x60y45', 'h3x60y46', 'h3x60y47', 'h3x60y48',
     'h3x60y49', 'h3x60y50', 'h3x60y51', 'h3x60y52', 'h3x60y53', 'h3x60y54',
     'h3x60y55', 'h3x60y56', 'h3x60y57'],
    ['h3x61y37', 'h3x61y38', 'h3x61y39', 'h3x61y40', 'h3x61y41', 'h3x61y42',
     'h3x61y43', 'h3x61y44', 'h3x61y45', 'h3x61y46', 'h3x61y47', 'h3x61y48',
     'h3x61y49', 'h3x61y50', 'h3x61y51', 'h3x61y52', 'h3x61y53', 'h3x61y54',
     'h3x61y55', 'h3x61y56', 'h3x61y57'],
    ['h3x62y37', 'h3x62y38', 'h3x62y39', 'h3x62y40', 'h3x62y41', 'h3x62y42',
     'h3x62y43', 'h3x62y44', 'h3x62y45', 'h3x62y46', 'h3x62y47', 'h3x62y48',
     'h3x62y49', 'h3x62y50', 'h3x62y51', 'h3x62y52', 'h3x62y53', 'h3x62y54',
     'h3x62y55', 'h3x62y56', 'h3x62y57'],
    ['h3x63y37', 'h3x63y38', 'h3x63y39', 'h3x63y40', 'h3x63y41', 'h3x63y42',
     'h3x63y43', 'h3x63y44', 'h3x63y45', 'h3x63y46', 'h3x63y47', 'h3x63y48',
     'h3x63y49', 'h3x63y50', 'h3x63y51', 'h3x63y52', 'h3x63y53', 'h3x63y54',
     'h3x63y55', 'h3x63y56', 'h3x63y57'],
    ['h3x64y37', 'h3x64y38', 'h3x64y39', 'h3x64y40', 'h3x64y41', 'h3x64y42',
     'h3x64y43', 'h3x64y44', 'h3x64y45', 'h3x64y46', 'h3x64y47', 'h3x64y48',
     'h3x64y49', 'h3x64y50', 'h3x64y51', 'h3x64y52', 'h3x64y53', 'h3x64y54',
     'h3x64y55', 'h3x64y56', 'h3x64y57'],
    ['h3x65y37', 'h3x65y38', 'h3x65y39', 'h3x65y40', 'h3x65y41', 'h3x65y42',
     'h3x65y43', 'h3x65y44', 'h3x65y45', 'h3x65y46', 'h3x65y47', 'h3x65y48',
     'h3x65y49', 'h3x65y50', 'h3x65y51', 'h3x65y52', 'h3x65y53', 'h3x65y54',
     'h3x65y55', 'h3x65y56', 'h3x65y57'],
    ['h3x66y37', 'h3x66y38', 'h3x66y39', 'h3x66y40', 'h3x66y41', 'h3x66y42',
     'h3x66y43', 'h3x66y44', 'h3x66y45', 'h3x66y46', 'h3x66y47', 'h3x66y48',
     'h3x66y49', 'h3x66y50', 'h3x66y51', 'h3x66y52', 'h3x66y53', 'h3x66y54',
     'h3x66y55', 'h3x66y56', 'h3x66y57'],
    ['h3x67y37', 'h3x67y38', 'h3x67y39', 'h3x67y40', 'h3x67y41', 'h3x67y42',
     'h3x67y43', 'h3x67y44', 'h3x67y45', 'h3x67y46', 'h3x67y47', 'h3x67y48',
     'h3x67y49', 'h3x67y50', 'h3x67y51', 'h3x67y52', 'h3x67y53', 'h3x67y54',
     'h3x67y55', 'h3x67y56', 'h3x67y57'],
    ['h3x68y37', 'h3x68y38', 'h3x68y39', 'h3x68y40', 'h3x68y41', 'h3x68y42',
     'h3x68y43', 'h3x68y44', 'h3x68y45', 'h3x68y46', 'h3x68y47', 'h3x68y48',
     'h3x68y49', 'h3x68y50', 'h3x68y51', 'h3x68y52', 'h3x68y53', 'h3x68y54',
     'h3x68y55', 'h3x68y56', 'h3x68y57']]

sector_width = 48
sector_height = 48


def readElev0():
    const_x = []
    for i in range(0, np.size(landscape0_file, axis=0)):
        # x-axis
        const_y = [];
        for j in range(0, np.size(landscape0_file, axis=1)):
            # y-axis
            fh = open(landscape_path+landscape0_file[i][j], "rb")
            data_read = bytearray(fh.read())
            fh.close()
            groundElevation = np.transpose(
                np.reshape(data_read, (sector_width*sector_height, 10)))[0]
            const_y.append(np.reshape(groundElevation,
                                      (sector_width, sector_height)))
        const_x.append(np.hstack(const_y))
    return np.vstack(const_x)


def readElev1():
    const_x = []
    for i in range(0, np.size(landscape1_file, axis=0)):
        # x-axis
        const_y = []
        for j in range(0, np.size(landscape1_file, axis=1)):
            # y-axis
            fh = open(landscape_path+landscape1_file[i][j], "rb")
            data_read = bytearray(fh.read())
            fh.close()
            groundElevation = np.transpose(
                np.reshape(data_read, (sector_width*sector_height, 10)))[0]
            const_y.append(np.reshape(groundElevation,
                                      (sector_width, sector_height)))
        const_x.append(np.hstack(const_y))
    return np.vstack(const_x)


def readElev2():
    const_x = []
    for i in range(0, np.size(landscape2_file, axis=0)):
        # x-axis
        const_y = []
        for j in range(0, np.size(landscape2_file, axis=1)):
            # y-axis
            fh = open(landscape_path+landscape2_file[i][j], "rb")
            data_read = bytearray(fh.read())
            fh.close()
            groundElevation = np.transpose(
                np.reshape(data_read, (sector_width*sector_height, 10)))[0]
            const_y.append(np.reshape(groundElevation,
                                      (sector_width, sector_height)))
        const_x.append(np.hstack(const_y))
    return np.vstack(const_x)


def readElev3():
    const_x = []
    for i in range(0, np.size(landscape3_file, axis=0)):
        # x-axis
        const_y = []
        for j in range(0, np.size(landscape3_file, axis=1)):
            # y-axis
            fh = open(landscape_path+landscape3_file[i][j], "rb")
            data_read = bytearray(fh.read())
            fh.close()
            groundElevation = np.transpose(
                np.reshape(data_read, (sector_width*sector_height, 10)))[0]
            const_y.append(np.reshape(groundElevation,
                                      (sector_width, sector_height)))
        const_x.append(np.hstack(const_y))
    return np.vstack(const_x)


"""
plot_mat0 = readElev3()
x = np.arange(0, np.size(plot_mat0, axis=0))
y = np.arange(0, np.size(plot_mat0, axis=1))
[X, Y] = np.meshgrid(x, y)
surf_plot(X, Y, plot_mat0/25.5)
"""
plot_mat0 = readElev0()
x = np.arange(0, np.size(plot_mat0, axis=0))
y = np.arange(0, np.size(plot_mat0, axis=1))
[Y, X] = np.meshgrid(y, x)  # because the C++ program uses x in the outer loop
X = np.reshape(X, (-1, np.size(X)))
Y = np.reshape(Y, (-1, np.size(Y)))
Z = np.reshape(plot_mat0, (-1, np.size(plot_mat0)))
X = np.array(X, dtype=np.float64)
Y = np.array(Y, dtype=np.float64)
Z = -np.array(Z/32, dtype=np.float64)
fh = open("X.dat", "wb")
fh.write(X.tobytes())
fh.close()
fh = open("Y.dat", "wb")
fh.write(Y.tobytes())
fh.close()
fh = open("Z.dat", "wb")
fh.write(Z.tobytes())
fh.close()
curr_path = "$HOME/git/rscclient/src/org/conf/utils/WorldMapPlot/"
os.system("build/Point " + curr_path + "X.dat "
          + curr_path + "Y.dat " + curr_path + "Z.dat "
          + str(len(x)) + " " + str(len(y)))
