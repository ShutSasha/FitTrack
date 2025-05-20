import { Injectable } from '@nestjs/common'
import * as nodemailer from 'nodemailer'

@Injectable()
export class EmailService {
  private transporter: nodemailer.Transporter

  constructor() {
    this.transporter = nodemailer.createTransport({
      host: process.env.SMTP_HOST,
      port: +process.env.SMTP_PORT,
      secure: false,
      auth: {
        user: process.env.SMTP_USER,
        pass: process.env.SMTP_PASSWORD,
      },
    })
  }

  async sendConfirmationEmail(email: string, token: string) {
    const confirmationUrl = `${process.env.NODE_ENV === 'production' ? process.env.APP_URL : 'http://localhost:5000/api'}/auth/confirm-email/${token}`

    await this.transporter.sendMail({
      from: `"Your App" <${process.env.SMTP_USER}>`,
      to: email,
      subject: 'Confirm Your Email',
      html: `
        <h1>Welcome!</h1>
        <p>Please confirm your email by clicking the link below:</p>
        <a href="${confirmationUrl}">${confirmationUrl}</a>
      `,
    })
  }
}
